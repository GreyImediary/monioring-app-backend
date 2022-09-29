package ru.therapyapp.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.UserDAO
import ru.therapyapp.users.db.Users

fun Application.configureAuth() {
    install(Authentication) {
        form("auth-form") {
            userParamName = "login"
            passwordParamName = "password"
            validate {credential ->
                try {
                    val user = dbQuery { UserDAO.find { Users.login eq  credential.name}.firstOrNull() }
                    if (user != null && user.password == credential.password) {
                        UserIdPrincipal(credential.name)
                    } else {
                        null
                    }
                } catch (e: ExposedSQLException) {
                    null
                }
            }
        }
    }
}