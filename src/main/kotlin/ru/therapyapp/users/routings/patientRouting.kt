package ru.therapyapp.users.routings

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.and
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.users.db.*
import ru.therapyapp.users.model.PatientBodyRequest
import ru.therapyapp.users.model.toPatient
import java.time.Instant

fun Application.configurePatientRouting() {
    routing {
        route("$API_VERSION/patient") {
            post {
                try {
                    val request = call.receive<PatientBodyRequest>()

                    val patient = dbQuery {
                        val mkb = MkbDAO.find { (MKBs.code eq request.mkb.code) and  (MKBs.name eq request.mkb.name) }.firstOrNull()

                        PatientDAO.new {
                            name = request.name
                            surname = request.surname
                            patronymic = request.patronymic
                            sex = request.sex
                            phoneNumber = request.phoneNumber
                            additionalPhoneNumber = request.additionalPhoneNumber
                            email = request.email
                            birthDate = Instant.parse(request.birthDate)
                            patientCardNumber = request.patientCardNumber
                            mkbDAO = mkb ?: MkbDAO.new {
                                name = request.mkb.name
                                code = request.mkb.code
                            }
                        }.toPatient()
                    }

                    call.respond(HttpStatusCode.OK, patient)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Пользователь не найден"))
                }
            }

            post("/withDoctor/{doctorId}") {
                try {
                    val doctorId = call.parameters["doctorId"]?.toInt() ?: -1

                    val request = call.receive<PatientBodyRequest>()

                    val patient = dbQuery {
                        val mkb = MkbDAO.find { (MKBs.code eq request.mkb.code) and  (MKBs.name eq request.mkb.name) }.firstOrNull()
                        PatientDAO.new {
                            name = request.name
                            surname = request.surname
                            patronymic = request.patronymic
                            sex = request.sex
                            phoneNumber = request.phoneNumber
                            additionalPhoneNumber = request.additionalPhoneNumber
                            email = request.email
                            birthDate = Instant.parse(request.birthDate)
                            patientCardNumber = request.patientCardNumber
                            mkbDAO = mkb ?: MkbDAO.new {
                                name = request.mkb.name
                                code = request.mkb.code
                            }
                        }
                    }

                    val doctor = dbQuery { DoctorDAO.findById(doctorId) }

                    dbQuery {
                        if (doctor != null) {
                            doctor.patients =
                                SizedCollection(doctor.patients.plus(patient))
                        }
                    }

                    val modelPatient = dbQuery { patient.toPatient() }

                    call.respond(HttpStatusCode.OK, modelPatient)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при добавлении пользователя"))
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

            /*get("/{userId}") {
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
            }*/
        }
    }
}