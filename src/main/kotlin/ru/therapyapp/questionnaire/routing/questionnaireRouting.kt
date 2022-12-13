package ru.therapyapp.questionnaire.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.questionnaire.db.OptionDAO
import ru.therapyapp.questionnaire.db.QuestionDAO
import ru.therapyapp.questionnaire.db.QuestionnaireDAO
import ru.therapyapp.questionnaire.db.Questionnaires
import ru.therapyapp.questionnaire.model.QuestionnaireRequestBody
import ru.therapyapp.questionnaire.model.toQuestionnaire
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.Doctors
import ru.therapyapp.users.db.PatientDAO

fun Application.questionnaireRouting() {
    routing {
        route("$API_VERSION/questionnaire") {
            post {
                try {

                    val requestBody = call.receive<QuestionnaireRequestBody>()

                    val doctor = dbQuery { DoctorDAO.findById(requestBody.doctorId) }

                    doctor?.let { doctorDAO ->

                        val patient = requestBody.forPatientId?.let {
                            dbQuery { PatientDAO.findById(requestBody.forPatientId) }
                        }

                        val questionnaire = dbQuery {
                            QuestionnaireDAO.new {
                                name = requestBody.name
                                byDoctor = doctorDAO
                                forPatient = patient
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
                    } ?: call.respond(HttpStatusCode.NotFound, "Доктор с таким id не найден")
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.InternalServerError, ResponseError("Ошибка при добавлении анкеты."))
                }
            }

            get {
                try {
                    val questionnaire = dbQuery { QuestionnaireDAO.all().map { it.toQuestionnaire() } }

                    call.respond(HttpStatusCode.OK, questionnaire)
                } catch (e: ExposedSQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ResponseError("Произошла ошибка при получении анкет")
                    )
                }
            }

            get("/byDoctor/{id}") {
                val doctorId = call.parameters["id"]?.toInt() ?: -1
                try {
                    val questionnaires = dbQuery { QuestionnaireDAO.find { Questionnaires.byDoctor eq doctorId }.map { it.toQuestionnaire() } }

                    call.respond(HttpStatusCode.OK, questionnaires)
                } catch (e: ExposedSQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ResponseError("Произошла ошибка при получении анкет")
                    )
                }
            }

            get("/byPatient/{id}") {
                val patientId = call.parameters["id"]?.toInt() ?: -1
                try {
                    val questionnaires = dbQuery {
                        val doctors = DoctorDAO.all().filter { it.patients.any { patientDAO -> patientDAO.id.value == patientId } }
                        val doctorIds = doctors.map { it.id }
                        QuestionnaireDAO.all()
                            .filter { doctorIds.contains(it.byDoctor.id) }
                            .filter { it.forPatient == null || it.forPatient?.id?.value == patientId }
                            .map { it.toQuestionnaire() }
                    }

                    call.respond(HttpStatusCode.OK, questionnaires)
                } catch (e: ExposedSQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ResponseError("Произошла ошибка при получении анкет")
                    )
                }
            }
        }
    }
}