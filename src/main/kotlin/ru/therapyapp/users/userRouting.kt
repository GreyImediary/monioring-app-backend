package ru.therapyapp.users

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.UserDAO
import ru.therapyapp.users.model.toGetUser

fun Application.configureUserRoting() {
    routing {
        route("$API_VERSION/user") {
            get {
                val users = dbQuery {
                    UserDAO.all().map { it.toGetUser() }
                }
                call.respond(HttpStatusCode.OK, users)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: -1
                val user = dbQuery {
                    UserDAO.findById(id)
                }

                if (user != null) {
                    call.respond(HttpStatusCode.OK, user.toGetUser())
                } else {
                    call.respond(HttpStatusCode.NotFound, "Пользователь не найден")
                }
            }
        }
    }
}