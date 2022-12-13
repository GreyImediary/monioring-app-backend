package ru.therapyapp.questionnaire_answered.model

import kotlinx.serialization.Serializable
import ru.therapyapp.questionnaire.model.Questionnaire
import ru.therapyapp.questionnaire.model.toQuestionnaire
import ru.therapyapp.questionnaire_answered.db.QuestionAnsweredDAO
import ru.therapyapp.questionnaire_answered.db.QuestionnaireAnsweredDAO
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toPatient

@Serializable
data class QuestionnaireAnswered(
    val patient: Patient,
    val questionnaire: Questionnaire,
    val date: String,
    val answers: List<QuestionAnswer>
)

@Serializable
data class QuestionnaireAnsweredRequestBody(
    val patientId: Int,
    val questionnaireId: Int,
    val date: String,
    val answers: List<QuestionAnswer>
)


@Serializable
data class QuestionAnswer(
    val title: String,
    val answer: String
)

fun QuestionnaireAnsweredDAO.toQuestionnaireAnswered() = QuestionnaireAnswered(
    patient = this.patientDAO.toPatient(),
    questionnaire = this.questionnaireDAO.toQuestionnaire(),
    date = this.date.toString(),
    answers = this.answeredQuestions.map { it.toQuestionAnswer() }
)

fun QuestionAnsweredDAO.toQuestionAnswer() = QuestionAnswer(
    title = this.title,
    answer = this.answer
)