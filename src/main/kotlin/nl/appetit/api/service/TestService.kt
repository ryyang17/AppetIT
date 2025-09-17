package nl.appetit.api.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration

@Service
class TestService {
    private val numbers: List<Int> = (1..100).toList()

    /**
     * Returns a Flux that emits numbers 1..100 with a delay of 100ms between each
     */
    fun streamNumbers(): Flux<Int> =
        Flux.fromIterable(numbers)
            .zipWith(Flux.interval(Duration.ofMillis(100)))
            .map { it.t1 } // extract the number
}