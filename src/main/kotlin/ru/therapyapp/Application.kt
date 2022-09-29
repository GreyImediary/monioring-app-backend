package ru.therapyapp

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.therapyapp.base_db.DatabaseFactory
import ru.therapyapp.auth.configureAuth
import ru.therapyapp.auth.configureAuthRouting
import ru.therapyapp.plugins.configureLogs
import ru.therapyapp.plugins.configureRouting
import ru.therapyapp.plugins.configureSecurity
import ru.therapyapp.plugins.configureSerialization
import ru.therapyapp.users.configureUserRoting
import ru.therapyapp.users.db.Doctors
import ru.therapyapp.users.db.Patients
import ru.therapyapp.users.db.Users

fun main() {

    transaction(DatabaseFactory.database) {
        SchemaUtils.create(Users, Patients, Doctors)
    }
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureLogs()
        configureUserRoting()
        configureSerialization()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
