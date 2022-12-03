package questionnaire.model

import kotlinx.serialization.Serializable
import questionnaire.db.*

@Serializable
data class Questionnaire(
    val name: String,
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
    name = this.name,
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