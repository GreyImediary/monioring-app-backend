package ru.therapyapp.index_selena_sledai.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.index_selena_sledai.db.SelenaSledaiIndexes
import ru.therapyapp.index_selena_sledai.db.SelenaSledaiIndexesDAO
import ru.therapyapp.index_selena_sledai.model.SelenaSledaiIndexBody
import ru.therapyapp.index_selena_sledai.model.toSelenaSledai
import ru.therapyapp.users.db.PatientDAO
import java.time.Instant

fun Application.configureSelenaSledaiRouting() {
    routing {
        route("$API_VERSION/sledai") {
            post {
                try {
                    val request = call.receive<SelenaSledaiIndexBody>()
                    val patient = dbQuery { PatientDAO.findById(request.patientId) }
                    patient?.let {
                        val sledaiIndex = dbQuery {
                            SelenaSledaiIndexesDAO.new {
                                patientDAO = it
                                answer = request.answer
                                date = Instant.parse(request.date)
                                value = request.sumValue
                            }.toSelenaSledai()
                        }

                        call.respond(HttpStatusCode.OK, sledaiIndex)
                    } ?: call.respond(HttpStatusCode.BadRequest, "Пациент не найден")
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при записи индекса"))
                }
            }

            get {
                try {
                    val indexes = dbQuery { SelenaSledaiIndexesDAO.all().map { it.toSelenaSledai() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении индексов"))
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val indexes = dbQuery { SelenaSledaiIndexesDAO.find {
                        SelenaSledaiIndexes.patient eq patientId
                    }.map { it.toSelenaSledai() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении индекса"))
                }
            }
        }
    }
}