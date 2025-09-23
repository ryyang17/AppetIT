package nl.appetit.api.controller

import nl.appetit.api.service.TestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration

@RestController
@RequestMapping("/numbers")
class TestController(
    private val testService: TestService
) {

    @GetMapping
    fun get(): Flux<Int> = testService.streamNumbers()
}
