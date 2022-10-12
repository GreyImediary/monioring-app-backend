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
import ru.therapyapp.users.routings.configureUserRoting
import ru.therapyapp.users.db.Doctors
import ru.therapyapp.users.db.DoctorsPatients
import ru.therapyapp.users.db.Patients
import ru.therapyapp.users.db.Users
import ru.therapyapp.users.routings.configureDoctorRouting
import ru.therapyapp.users.routings.configurePatientRouting

fun main() {

    transaction(DatabaseFactory.database) {
        SchemaUtils.create(Users, Patients, Doctors, DoctorsPatients)
    }
    embeddedServer(Netty, port = 8080, host = "192.168.0.15") {
        configureAuth()
        configureAuthRouting()
        configureLogs()
        configureUserRoting()
        configureSerialization()
        configureSecurity()
        configureRouting()
        configureDoctorRouting()
        configurePatientRouting()
    }.start(wait = true)
}
