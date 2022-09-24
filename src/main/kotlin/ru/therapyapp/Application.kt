package ru.therapyapp

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.therapyapp.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureLogs()
        configureSerialization()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
