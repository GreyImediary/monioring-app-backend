package ru.therapyapp.index_bvas.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.index_bvas.bd.BvasIndexDAO
import ru.therapyapp.index_bvas.bd.BvasIndexes
import ru.therapyapp.index_bvas.model.BvasRequestBody
import ru.therapyapp.index_bvas.model.toBvasIndex
import ru.therapyapp.index_bvas.model.toStringBvasQuestion
import ru.therapyapp.users.db.PatientDAO
import java.time.Instant

fun Application.configureBvasRouting() {
    routing {
        route("$API_VERSION/bvas") {
            post {
                try {
                    val request = call.receive<BvasRequestBody>()
                    val patient = dbQuery { PatientDAO.findById(request.patientId) }
                    patient?.let {
                        println("AAAAAAAAAAAAAAAAAAAA")
                        val bvasIndex = dbQuery {
                            println("AAAAAAAAAAAAAAAAAAAA 2")

                            println(it.id.value.toString())
                            println(request.toString())

                            println("AAAAAAAAAAAAAAAAAAAA 3")
                            BvasIndexDAO.new {
                                patientDAO = it
                                question1 = toStringBvasQuestion(request.question1)
                                question2 = toStringBvasQuestion(request.question2)
                                question3 = toStringBvasQuestion(request.question3)
                                question4 = toStringBvasQuestion(request.question4)
                                question5 = toStringBvasQuestion(request.question5)
                                question6 = toStringBvasQuestion(request.question6)
                                question7 = toStringBvasQuestion(request.question7)
                                question8 = toStringBvasQuestion(request.question8)
                                question9 = toStringBvasQuestion(request.question9)
                                date = Instant.parse(request.date)
                                value = request.sumValue
                            }.toBvasIndex()
                        }

                        println("AAAAAAAAAAAAAAAAAAAA 4")
                        call.respond(HttpStatusCode.OK, bvasIndex)
                    } ?: call.respond(HttpStatusCode.BadRequest, "?????????????? ???? ????????????")
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("???????????? ?????? ???????????? ??????????????"))
                } catch (e: Exception) {
                    println(e.message)
                    println(e.localizedMessage)
                    call.respond(HttpStatusCode.BadRequest, ResponseError("???????????? ?????? ???????????? ??????????????"))
                }
            }

            get {
                try {
                    val indexes = dbQuery { BvasIndexDAO.all().map { it.toBvasIndex() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("???????????? ?????? ?????????????????? ????????????????"))
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val indexes = dbQuery { BvasIndexDAO.find { BvasIndexes.patient eq patientId }.map { it.toBvasIndex() } }

                    call.respond(HttpStatusCode.OK, indexes)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("???????????? ?????? ?????????????????? ??????????????"))
                } catch (e: Exception) {
                    println(e.message)
                    println(e.localizedMessage)
                    println(e.stackTrace.forEach { println(it.toString()) })
                    call.respond(HttpStatusCode.BadRequest, ResponseError("???????????? ?????? ???????????? ??????????????"))
                }
            }
        }
    }
}