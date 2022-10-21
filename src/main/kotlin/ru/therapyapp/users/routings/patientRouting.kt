package ru.therapyapp.users.routings

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients
import ru.therapyapp.users.db.UserDAO
import ru.therapyapp.users.model.PatientBodyRequest
import ru.therapyapp.users.model.toPatient
import java.time.Instant

fun Application.configurePatientRouting() {
    routing {
        route("$API_VERSION/patient") {
            post {
                try {
                    val request = call.receive<PatientBodyRequest>()
                    val user = dbQuery { UserDAO.findById(request.userId) }
                    user?.let {
                        val patient = dbQuery {
                            PatientDAO.new {
                                userDAO = it
                                name = request.name
                                surname = request.surname
                                patronymic = request.patronymic
                                sex = request.sex
                                phoneNumber = request.phoneNumber
                                additionalPhoneNumber = request.additionalPhoneNumber
                                email = request.email
                                birthDate = Instant.parse(request.birthDate)
                                patientCardNumber = request.patientCardNumber
                            }.toPatient()
                        }

                        call.respond(HttpStatusCode.OK, patient)
                    } ?: call.respond(HttpStatusCode.NotFound, ResponseError("Данные пациента не найдены"))
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Пользователь не найден"))
                }
            }

            get {
                try {
                    val patients = dbQuery { PatientDAO.all().map { it.toPatient() } }

                    call.respond(HttpStatusCode.OK, patients)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка во время получения"))
                }
            }

            get("/{userId}") {
                val userId = call.parameters["userId"]?.toInt() ?: -1
                try {
                    val patient = dbQuery { PatientDAO.find { Patients.user eq userId }.firstOrNull()?.toPatient() }
                    if (patient != null) {
                        call.respond(HttpStatusCode.OK, patient)
                    } else {
                        call.respond(HttpStatusCode.NotFound, ResponseError("Данные не заполнены"))
                    }
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.NotFound, ResponseError("Данные не заполнены"))
                }
            }
        }
    }
}