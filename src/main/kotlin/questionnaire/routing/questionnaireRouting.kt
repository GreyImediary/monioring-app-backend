package questionnaire.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import questionnaire.db.OptionDAO
import questionnaire.db.QuestionDAO
import questionnaire.db.QuestionnaireDAO
import questionnaire.model.Questionnaire
import questionnaire.model.toQuestionnaire
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.PatientDAO

fun Application.questionnaireRouting() {
    routing {
        route("/questionnaire") {
            post {
                try {
                    val requestBody = call.receive<Questionnaire>()

                    val questionnaire = dbQuery {
                        QuestionnaireDAO.new {
                            name = requestBody.name
                            forPatient = PatientDAO.findById(requestBody.forPatientId ?: -1)
                        }
                    }

                    dbQuery {
                        requestBody.questions.map { questionRequest ->
                            val question = QuestionDAO.new {
                                questionnaireDAO = questionnaire
                                title = questionRequest.title
                                type = questionRequest.questionType
                            }

                            questionRequest.options.forEach { option ->
                                OptionDAO.new {
                                    questionDAO = question
                                    description = option.description
                                }
                            }
                        }
                    }

                    call.respond(HttpStatusCode.OK)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.InternalServerError, ResponseError("Ошибка при добавлении анкеты."))
            }
        }

        get {
            try {
                val questionnaire = dbQuery { QuestionnaireDAO.all().map { it.toQuestionnaire() } }

                call.respond(HttpStatusCode.OK, questionnaire)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.InternalServerError, ResponseError("Произошла ошибка при получении анкет"))
            }
        }
    }
}
}