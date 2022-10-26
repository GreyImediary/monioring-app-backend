package ru.therapyapp.index_basdai.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.index_basdai.bd.BasdaiIndexDAO
import ru.therapyapp.index_basdai.bd.BasdaiIndexes
import ru.therapyapp.index_basdai.model.BasdaiIndexRequestBody
import ru.therapyapp.index_basdai.model.toBasdaiIndex
import ru.therapyapp.users.db.PatientDAO
import java.time.Instant

fun Application.configureBasdaiRouting() {
    routing {
        route("$API_VERSION/basdai") {
            post {
                try {
                    val request = call.receive<BasdaiIndexRequestBody>()
                    val patient = dbQuery { PatientDAO.findById(request.patientId) }
                    patient?.let {
                        val basdaiIndex = dbQuery {
                            BasdaiIndexDAO.new {
                                patientDAO = it
                                question1 = request.question1Value
                                question2 = request.question2Value
                                question3 = request.question3Value
                                question4 = request.question4Value
                                question5 = request.question5Value
                                question6 = request.question6Value
                                date = Instant.parse(request.date)
                                value = request.sumValue
                            }.toBasdaiIndex()
                        }

                        call.respond(HttpStatusCode.OK, basdaiIndex)
                    } ?: call.respond(HttpStatusCode.BadRequest, "Пациент не найден")
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при записи индекса"))
                }
            }

            get {
                try {
                    val indexes = dbQuery { BasdaiIndexDAO.all().map { it.toBasdaiIndex() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении индексов"))
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val indexes = dbQuery { BasdaiIndexDAO.find { BasdaiIndexes.patient eq patientId }.map { it.toBasdaiIndex() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении индекса"))
                }
            }
        }
    }
}