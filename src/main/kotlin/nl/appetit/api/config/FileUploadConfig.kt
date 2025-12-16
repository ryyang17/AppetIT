package nl.appetit.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class FileUploadConfig : WebFluxConfigurer {
    
    @Value("\${file.upload.dir:uploads/products}")
    lateinit var uploadDir: String
    
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Dit zorgt ervoor dat http://localhost:8080/uploads/products/foto.jpg
        // verwijst naar de uploads/products/ folder op je disk
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/")
    }
}