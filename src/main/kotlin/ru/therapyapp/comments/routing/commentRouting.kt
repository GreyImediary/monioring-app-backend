package ru.therapyapp.comments.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.comments.db.CommentDAO
import ru.therapyapp.comments.db.Comments
import ru.therapyapp.comments.model.CommentRequestBody
import ru.therapyapp.comments.model.toComment
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.PatientDAO
import java.time.Instant

fun Application.configureCommentRouting() {
    routing {
        route("$API_VERSION/comment") {
            post {
                try {
                    val request = call.receive<CommentRequestBody>()
                    val patient = dbQuery { PatientDAO.findById(request.patientId) }
                    val doctor = dbQuery { DoctorDAO.findById(request.doctorId) }

                    if (patient != null && doctor != null) {
                        val comments = dbQuery {
                            CommentDAO.new {
                                patientDAO = patient
                                doctorDAO = doctor
                                comment = request.comment
                                date = Instant.parse(request.date)
                            }

                            CommentDAO.find { Comments.patient eq patient.id }.map { it.toComment() }.reversed()
                        }

                        call.respond(HttpStatusCode.OK, comments)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Пользователь не найден")
                    }
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при записи комментария"))
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val comments = dbQuery { CommentDAO.find { Comments.patient eq patientId }.map { it.toComment() }.reversed() }

                    call.respond(HttpStatusCode.OK, comments)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении комментариев"))
                }
            }
        }
    }
}