package nl.appetit.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppetITApi

fun main(args: Array<String>) {
    val context = runApplication<AppetITApi>(*args)
}