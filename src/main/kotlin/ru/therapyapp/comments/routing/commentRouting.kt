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
import ru.therapyapp.users.db.PatientDAO

fun Application.configureCommentRouting() {
    routing {
        route("$API_VERSION/comment") {
            post {
                try {
                    val request = call.receive<CommentRequestBody>()
                    val patient = dbQuery { PatientDAO.findById(request.patientId) }
                    patient?.let { patientDao ->
                        val comments = dbQuery {
                            CommentDAO.new {
                                patientDAO = patient
                                comment = request.comment
                            }

                            CommentDAO.find { Comments.patient eq patientDao.id }.map { it.toComment() }
                        }

                        call.respond(HttpStatusCode.OK, comments)
                    } ?: call.respond(HttpStatusCode.NotFound, "Пользователь не найден")
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при записи комментария"))
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val comments = dbQuery { CommentDAO.find { Comments.patient eq patientId }.map { it.toComment() } }

                    call.respond(HttpStatusCode.OK, comments)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка при получении комментариев"))
                }
            }
        }
    }
}