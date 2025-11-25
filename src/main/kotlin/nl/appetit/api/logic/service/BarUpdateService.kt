package nl.appetit.api.logic.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.concurrent.ConcurrentHashMap

@Service
class BarUpdateService {
    private val sinks: MutableMap<String, FluxSink<String>> = ConcurrentHashMap()

    fun subscribe(restaurantId: String): Flux<String> {
        return Flux.create { sink ->
            sinks[restaurantId] = sink
        }.publish().autoConnect()
    }

    // Server pushes updates
    fun pushUpdate(restaurantId: String, message: String) {
        sinks[restaurantId]?.next(message)
    }
}