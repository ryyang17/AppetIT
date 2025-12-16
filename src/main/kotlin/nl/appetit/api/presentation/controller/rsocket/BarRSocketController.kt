package nl.appetit.api.presentation.controller.rsocket

import nl.appetit.api.logic.service.BarUpdateService
import nl.appetit.api.logic.service.Payload
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class BarRSocketController(
    private val barUpdateService: BarUpdateService
) {
    @MessageMapping("updates.bar")
    fun streamUpdate(): Flux<Payload> =
        barUpdateService.subscribe("1")
}