package nl.appetit.api.logic.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.concurrent.ConcurrentHashMap

data class Payload(
    val message: String
)

@Service
class BarUpdateService {
    private val sinks: MutableMap<String, FluxSink<Payload>> = ConcurrentHashMap()

    fun subscribe(restaurantId: String): Flux<Payload> {
        return Flux.create { sink ->
            sinks[restaurantId] = sink
        }.publish().autoConnect()
    }

    // Server pushes updates
    fun pushUpdate(restaurantId: String, message: String) {
        val payload = Payload(message)

        sinks[restaurantId]?.next(payload)
    }
}