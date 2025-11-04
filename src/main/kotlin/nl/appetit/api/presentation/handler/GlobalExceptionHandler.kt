package nl.appetit.api.presentation.handler

import nl.appetit.api.logic.exception.BadRequestException
import nl.appetit.api.logic.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

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

    @ExceptionHandler
    fun handleGenericException(e: Exception): Mono<ResponseEntity<Map<String, String>>> {
        val body = mapOf("errors" to "Internal Server Error")
        logger.error(e.message, e)
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body))
    }
}
