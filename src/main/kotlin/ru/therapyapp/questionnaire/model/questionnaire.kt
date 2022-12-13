package ru.therapyapp.questionnaire.model

import kotlinx.serialization.Serializable
import ru.therapyapp.questionnaire.db.OptionDAO
import ru.therapyapp.questionnaire.db.QuestionDAO
import ru.therapyapp.questionnaire.db.QuestionType
import ru.therapyapp.questionnaire.db.QuestionnaireDAO
import ru.therapyapp.users.model.Doctor
import ru.therapyapp.users.model.toDoctor

@Serializable
data class Questionnaire(
    val id: Int,
    val name: String,
    val doctor: Doctor,
    val forPatientId: Int?,
    val questions: List<Question>
)

@Serializable
data class QuestionnaireRequestBody(
    val name: String,
    val doctorId: Int,
    val forPatientId: Int?,
    val questions: List<Question>
)

@Serializable
data class Question(
    val title: String,
    val questionType: QuestionType,
    val options: List<Option>
)

@Serializable
data class Option(
    val description: String
)

fun QuestionnaireDAO.toQuestionnaire() = Questionnaire(
    id = this.id.value,
    name = this.name,
    doctor = this.byDoctor.toDoctor(),
    forPatientId = this.forPatient?.id?.value,
    questions = this.questions.map { it.toQuestion() }
)

fun QuestionDAO.toQuestion() = Question(
    title = this.title,
    questionType = this.type,
    options = this.options.map { it.toOption() }
)

fun OptionDAO.toOption() = Option(
    description = this.description
)