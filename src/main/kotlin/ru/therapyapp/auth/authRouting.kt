package ru.therapyapp.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.users.db.*
import ru.therapyapp.users.model.UserCreationBody
import ru.therapyapp.users.model.toUser

fun Application.configureAuthRouting() {
    routing {
        authenticate("auth-form") {
            post("$API_VERSION/auth") {
                try {
                    val user = dbQuery {
                        UserDAO.find { Users.login eq (call.principal<UserIdPrincipal>()?.name ?: "") }.firstOrNull()
                    }

                    user?.let {
                        call.respond(HttpStatusCode.OK, user.toUser())
                    } ?: call.respond(HttpStatusCode.NotFound, "Пользователь не найден")
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Произошла ошибка авторизации, проверьте логин и пароль"))
                }
            }
        }

        post("$API_VERSION/register") {
            try {
                val userBody = call.receive<UserCreationBody>()
                val user = dbQuery {
                    UserDAO.new {
                        login = userBody.login
                        password = userBody.password
                        userType = userBody.userType
                    }
                }

                call.respond(HttpStatusCode.OK, user.toUser())
            } catch (exception: ExposedSQLException) {
                call.respond(HttpStatusCode.BadRequest, ResponseError("Пользователь уже существует"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ResponseError("Проверьте введённые данные"))
            }
        }
    }
}