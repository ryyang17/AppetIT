package nl.appetit.api.logic.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant

@Service
class FileStorageService {
    
    @Value("\${file.upload.dir:uploads/products}")
    private lateinit var uploadDir: String
    
    // Toegestane bestandstypes
    private val allowedExtensions = listOf("jpg", "jpeg", "png", "webp")
    
    fun storeFile(productId: Int, filePart: FilePart): Mono<String> {
        // 1. Valideer bestandstype
        val filename = filePart.filename()
        val extension = filename.substringAfterLast(".", "")
        
        if (!allowedExtensions.contains(extension.lowercase())) {
            return Mono.error(IllegalArgumentException("Alleen jpg, png, webp toegestaan"))
        }
        
        // 2. Genereer unieke bestandsnaam: {productId}_{timestamp}.{extension}
        val timestamp = Instant.now().toEpochMilli()
        val newFilename = "${productId}_${timestamp}.${extension}"
        
        // 3. Maak het volledige pad
        val uploadPath = Paths.get(uploadDir)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }
        
        val filePath = uploadPath.resolve(newFilename)
        
        // 4. Sla het bestand op
        return filePart.transferTo(filePath)
            .then(Mono.just("/uploads/products/$newFilename"))
    }
    
    fun deleteFile(imageUrl: String): Mono<Void> {
        return Mono.fromRunnable {
            // imageUrl is bijv: /uploads/products/5_123456.jpg
            // We moeten alleen het bestand verwijderen, niet het hele pad
            val filename = imageUrl.substringAfterLast("/")
            val filePath = Paths.get(uploadDir, filename)
            
            if (Files.exists(filePath)) {
                Files.delete(filePath)
            }
        }
    }
}