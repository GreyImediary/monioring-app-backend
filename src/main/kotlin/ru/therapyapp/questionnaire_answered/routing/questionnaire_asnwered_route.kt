package ru.therapyapp.questionnaire_answered.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.questionnaire.db.QuestionnaireDAO
import ru.therapyapp.questionnaire_answered.db.QuestionAnsweredDAO
import ru.therapyapp.questionnaire_answered.db.QuestionnaireAnsweredDAO
import ru.therapyapp.questionnaire_answered.db.QuestionnairesAnswered
import ru.therapyapp.questionnaire_answered.model.QuestionnaireAnsweredRequestBody
import ru.therapyapp.questionnaire_answered.model.toQuestionnaireAnswered
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
                    val requestBody = call.receive<QuestionnaireAnsweredRequestBody>()

                    val patient = dbQuery { PatientDAO.findById(requestBody.patientId) }
                    val questionnaire = dbQuery { QuestionnaireDAO.findById(requestBody.questionnaireId) }

                    if (patient != null && questionnaire != null) {
                        dbQuery {
                            val questionnaireAnswered = QuestionnaireAnsweredDAO.new {
                                patientDAO = patient
                                questionnaireDAO = questionnaire
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
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ResponseError("Добавить ответ не получилось. Пациент или анкета не найдены")
                        )
                    }
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

            get("/byQuestionnaireId/{id}") {
                val questionnaireId = call.parameters["id"]?.toInt() ?: -1

                try {
                    val questionnairesAnswered = dbQuery {
                        QuestionnaireAnsweredDAO.find {
                            QuestionnairesAnswered.questionnaireId eq questionnaireId
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