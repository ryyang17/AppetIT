package nl.appetit.api.presentation.handler

import nl.appetit.api.logic.exception.BadRequestException
import nl.appetit.api.logic.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.core.codec.DecodingException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import java.lang.IllegalArgumentException

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler
    fun handleNotFoundException(e: NotFoundException): Mono<ResponseEntity<Map<String, String>>> {
        val body = mapOf("errors" to e.message.orEmpty())
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(body))
    }

    @ExceptionHandler
    fun handleBadRequestException(e: BadRequestException): Mono<ResponseEntity<Map<String, String>>> {
        val body = mapOf("errors" to e.message.orEmpty())
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body))
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationError(ex: WebExchangeBindException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("errors" to errors))
    }

    @ExceptionHandler(
        DecodingException::class,
        InvalidFormatException::class,
        ServerWebInputException::class
    )
    fun handleDecodingErrors(e: Exception): ResponseEntity<Map<String, String>> {
        val message = when (e) {
            is InvalidFormatException -> "Invalid value for field: ${e.path.joinToString(".") { it.fieldName ?: "?" }}"
            is ServerWebInputException -> e.reason ?: "Invalid request input"
            else -> "Invalid request body"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("errors" to message))
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(e: DataIntegrityViolationException): ResponseEntity<Map<String, String>> {
        logger.warn("Data integrity violation", e)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("errors" to "Invalid data: ${e.mostSpecificCause?.message ?: e.message}"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("errors" to (e.message ?: "Invalid request")))
    }

    @ExceptionHandler
    fun handleGenericException(e: Exception): Mono<ResponseEntity<Map<String, String>>> {
        val errorMessage = e.message ?: "Internal Server Error"
        val body = mapOf("errors" to errorMessage)
        logger.error("Unhandled exception: {}", errorMessage, e)
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body))
    }
}
