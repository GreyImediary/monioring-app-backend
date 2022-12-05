package questionnaire_answered.model

import kotlinx.serialization.Serializable
import questionnaire_answered.db.QuestionAnsweredDAO
import questionnaire_answered.db.QuestionnaireAnsweredDAO

@Serializable
data class QuestionnaireAnswered(
    val patientId: Int,
    val name: String,
    val date: String,
    val answers: List<QuestionAnswer>
)

@Serializable
data class QuestionAnswer(
    val title: String,
    val answer: String
)

fun QuestionnaireAnsweredDAO.toQuestionnaireAnswered() = QuestionnaireAnswered(
    patientId = this.patientDAO.id.value,
    name = this.name,
    date = this.date.toString(),
    answers = this.answeredQuestions.map { it.toQuestionAnswer() }
)

fun QuestionAnsweredDAO.toQuestionAnswer() = QuestionAnswer(
    title = this.title,
    answer = this.answer
)