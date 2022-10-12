package ru.therapyapp.users.routings

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.*
import ru.therapyapp.users.model.DoctorRequestBody
import ru.therapyapp.users.model.toDoctor

fun Application.configureDoctorRouting() {
    routing {
        route("$API_VERSION/doctor") {
            post {
                try {
                    val request = call.receive<DoctorRequestBody>()
                    val user = dbQuery { UserDAO.findById(request.userId) }
                    user?.let { foundUser ->
                        val doctor = dbQuery {
                            DoctorDAO.new {
                                userDAO = foundUser
                                name = request.name
                                surname = request.surname
                                patronymic = request.patronymic
                                sex = request.sex
                                phoneNumber = request.phoneNumber
                                email = request.email
                            }.toDoctor()
                        }
                        call.respond(HttpStatusCode.OK, doctor)
                    } ?: call.respond(HttpStatusCode.NotFound, ResponseError("Данные доктора не найдены"))
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Пользователь не найден"))
                }
            }

            /*get("/{id}") {
                try {
                    val doctor = dbQuery { DoctorDAO.findById(call.parameters["id"]?.toInt() ?: -1) }
                    doctor?.let {
                        call.respond(HttpStatusCode.OK, doctor.toDoctor())
                    }
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.NotFound, ResponseError("Доктор не найден"))
                }
            }*/

            get("/{userId}") {
                val userId = call.parameters["userId"]?.toInt() ?: -1
                try {
                    val doctor = dbQuery { DoctorDAO.find { Doctors.user eq userId }.firstOrNull()?.toDoctor() }
                    if (doctor != null) {
                        call.respond(HttpStatusCode.OK, doctor)
                    } else {
                        call.respond(HttpStatusCode.NotFound, ResponseError("Данные не заполнены"))
                    }
                }
                catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.NotFound, ResponseError("Данные не заполнены"))
                }
            }
        }
    }
}