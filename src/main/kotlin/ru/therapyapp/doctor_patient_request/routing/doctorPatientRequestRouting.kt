package ru.therapyapp.doctor_patient_request.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.and
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.doctor_patient_request.db.DocPatientRequestStatus
import ru.therapyapp.doctor_patient_request.db.DoctorPatientRequestDAO
import ru.therapyapp.doctor_patient_request.db.DoctorPatientRequests
import ru.therapyapp.doctor_patient_request.model.DoctorPatientRequestCreateBody
import ru.therapyapp.doctor_patient_request.model.DoctorPatientRequestUpdateBody
import ru.therapyapp.doctor_patient_request.model.toDoctorPatientRequest
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.PatientDAO

fun Application.configureDoctorPatientRequestRouting() {
    routing {
        route("$API_VERSION/request") {
            post {
                try {
                    val requestBody = call.receive<DoctorPatientRequestCreateBody>()
                    val doctor = dbQuery { DoctorDAO.findById(requestBody.doctorId) }
                    val patient = dbQuery { PatientDAO.findById(requestBody.patientId) }

                    if (patient != null && doctor != null) {
                        val isAdded = dbQuery { doctor.patients.find { it.id == patient.id } != null }
                        val isRequested = dbQuery {
                            DoctorPatientRequestDAO.find { DoctorPatientRequests.doctor eq doctor.id and
                                    (DoctorPatientRequests.patient eq patient.id)
                            }.firstOrNull()
                        }

                        if (isAdded || (isRequested != null && isRequested.status != DocPatientRequestStatus.DECLINED)) {
                            call.respond(HttpStatusCode.BadGateway, ResponseError("Заявка уже создана или принята пациентом"))
                            return@post
                        }

                        val requests = dbQuery {
                            DoctorPatientRequestDAO.new {
                                doctorDAO = doctor
                                patientDAO = patient
                                status = requestBody.status
                            }.toDoctorPatientRequest()

                            DoctorPatientRequestDAO.all().filter { it.doctorDAO.id == doctor.id }.map { it.toDoctorPatientRequest() }
                        }

                        call.respond(HttpStatusCode.OK, requests)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Не найден пользователь или врач")
                    }
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при формировании заявки"))
                }
            }

            delete("/{id}") {
                try {
                    val requestId = call.parameters["id"]?.toInt() ?: -1

                    val requests = dbQuery {
                        DoctorPatientRequestDAO.findById(requestId)?.delete()
                        DoctorPatientRequestDAO.all().map { it.toDoctorPatientRequest() }
                    }

                    call.respond(HttpStatusCode.OK, requests)

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Ошибка при удалении заявки")
                }
            }

            get("/byDoctor/{id}") {
                val doctorId = call.parameters["id"]?.toInt() ?: -1
                try {
                    val requests = dbQuery {
                        DoctorPatientRequestDAO.find { DoctorPatientRequests.doctor eq doctorId }
                            .map { it.toDoctorPatientRequest() }
                    }

                    call.respond(HttpStatusCode.OK, requests)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении списка заявок"))
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val requests = dbQuery {
                        DoctorPatientRequestDAO.find { DoctorPatientRequests.patient eq patientId }
                            .map { it.toDoctorPatientRequest() }
                    }

                    call.respond(HttpStatusCode.OK, requests)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении списка заявок"))

                }
            }

            put {
                try {
                    val updateBody = call.receive<DoctorPatientRequestUpdateBody>()

                    val request = dbQuery { DoctorPatientRequestDAO.findById(updateBody.id) }

                    if (request != null) {
                        val requests = dbQuery {
                            request.status = updateBody.status
                            if (updateBody.status == DocPatientRequestStatus.ACCEPTED) {
                                request.doctorDAO.patients =
                                    SizedCollection(request.doctorDAO.patients.plus(request.patientDAO))
                            }
                            DoctorPatientRequestDAO.all().map { it.toDoctorPatientRequest() }
                        }

                        call.respond(HttpStatusCode.OK, requests)

                    } else {
                        call.respond(HttpStatusCode.NotFound, "Заявка не найдена")
                        return@put
                    }
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при обновлении заявки"))
                }
            }
        }
    }
}