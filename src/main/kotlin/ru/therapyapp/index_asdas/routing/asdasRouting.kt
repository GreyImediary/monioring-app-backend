package ru.therapyapp.index_asdas.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.index_asdas.db.AsdasIndexDao
import ru.therapyapp.index_asdas.db.AsdasIndexes
import ru.therapyapp.index_asdas.model.AsdasIndexRequestBody
import ru.therapyapp.index_asdas.model.toAsdasIndex
import ru.therapyapp.users.db.PatientDAO
import java.time.Instant

fun Application.configureAsdasRouting() {
    routing {
        route("$API_VERSION/asdas") {
            post {
                try {
                    val request = call.receive<AsdasIndexRequestBody>()
                    val patient = dbQuery { PatientDAO.findById(request.patientId) }
                    patient?.let {
                        val asdasIndex = dbQuery {
                            AsdasIndexDao.new {
                                patientDAO = it
                                question1 = request.question1
                                question2 = request.question2
                                question3 = request.question3
                                question4 = request.question4
                                srbSoeType = request.srbSoeType
                                srbSoeValue = request.srbSoeValue
                                date = Instant.parse(request.date)
                                value = request.sumValue
                            }.toAsdasIndex()
                        }

                        call.respond(HttpStatusCode.OK, asdasIndex)
                    } ?: call.respond(HttpStatusCode.BadRequest, "Пациент не найден")
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при записи индекса"))
                }
            }

            get {
                try {
                    val indexes = dbQuery { AsdasIndexDao.all().map { it.toAsdasIndex() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении индексов"))
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val indexes = dbQuery { AsdasIndexDao.find { AsdasIndexes.patient eq patientId }.map { it.toAsdasIndex() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении индекса"))
                }
            }
        }
    }
}