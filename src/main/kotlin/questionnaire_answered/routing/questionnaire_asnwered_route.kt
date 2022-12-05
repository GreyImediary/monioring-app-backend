package questionnaire_answered.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import questionnaire_answered.db.QuestionAnsweredDAO
import questionnaire_answered.db.QuestionnaireAnsweredDAO
import questionnaire_answered.db.QuestionnairesAnswered
import questionnaire_answered.model.QuestionnaireAnswered
import questionnaire_answered.model.toQuestionnaireAnswered
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.PatientDAO
import java.time.Instant

fun Application.questionnaireAnsweredRouting() {
    routing {
        route("$API_VERSION/questionnaireAnswered") {
            post {
                try {
                    val requestBody = call.receive<QuestionnaireAnswered>()

                    val patient = dbQuery { PatientDAO.findById(requestBody.patientId) }

                    patient?.let {
                        dbQuery {
                            val questionnaireAnswered = QuestionnaireAnsweredDAO.new {
                                patientDAO = patient
                                name = requestBody.name
                                date = Instant.parse(requestBody.date)
                            }

                            requestBody.answers.forEach { questionAnswer ->
                                QuestionAnsweredDAO.new {
                                    questionnaireAnsweredDAO = questionnaireAnswered
                                    title = questionAnswer.title
                                    answer = questionAnswer.answer
                                }
                            }
                        }
                        call.respond(HttpStatusCode.OK)
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        ResponseError("Добавить ответ не получилось. Такой пациент не найден")
                    )
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, "Ошибка придобавлении анкеты")
                }
            }

            get {
                try {
                    val questionnairesAnswered = dbQuery {
                        QuestionnaireAnsweredDAO.all().map {
                            it.toQuestionnaireAnswered()
                        }
                    }

                    call.respond(HttpStatusCode.OK, questionnairesAnswered)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, "Ошибка при получении анкет")
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val questionnairesAnswered = dbQuery {
                        QuestionnaireAnsweredDAO.find {
                            QuestionnairesAnswered.patient eq patientId
                        }.map {
                            it.toQuestionnaireAnswered()
                        }
                    }

                    call.respond(HttpStatusCode.OK, questionnairesAnswered)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, "Ошибка при получении анкет")
                }
            }
        }
    }
}