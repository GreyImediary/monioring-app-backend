package ru.therapyapp.index_bvas.model

import kotlinx.serialization.Serializable
import ru.therapyapp.index_bvas.bd.BvasIndexDAO
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toPatient

@Serializable
data class BvasIndex(
    val patient: Patient,
    val question1: List<QuestionAnswer>,
    val question2: List<QuestionAnswer>,
    val question3: List<QuestionAnswer>,
    val question4: List<QuestionAnswer>,
    val question5: List<QuestionAnswer>,
    val question6: List<QuestionAnswer>,
    val question7: List<QuestionAnswer>,
    val question8: List<QuestionAnswer>,
    val question9: List<QuestionAnswer>,
    val date: String,
    val sumValue: Int,
) {
    @Serializable
    data class QuestionAnswer(
        val title: String,
        val value: Int,
    )
}

@Serializable
data class BvasRequestBody(
    val patientId: Int,
    val question1: List<BvasIndex.QuestionAnswer>,
    val question2: List<BvasIndex.QuestionAnswer>,
    val question3: List<BvasIndex.QuestionAnswer>,
    val question4: List<BvasIndex.QuestionAnswer>,
    val question5: List<BvasIndex.QuestionAnswer>,
    val question6: List<BvasIndex.QuestionAnswer>,
    val question7: List<BvasIndex.QuestionAnswer>,
    val question8: List<BvasIndex.QuestionAnswer>,
    val question9: List<BvasIndex.QuestionAnswer>,
    val date: String,
    val sumValue: Int,
)

fun BvasIndexDAO.toBvasIndex() = BvasIndex(
    patient = this.patientDAO.toPatient(),
    question1 = toBvasQuestionAnswers(this.question1),
    question2 = toBvasQuestionAnswers(this.question2),
    question3 = toBvasQuestionAnswers(this.question3),
    question4 = toBvasQuestionAnswers(this.question4),
    question5 = toBvasQuestionAnswers(this.question5),
    question6 = toBvasQuestionAnswers(this.question6),
    question7 = toBvasQuestionAnswers(this.question7),
    question8 = toBvasQuestionAnswers(this.question8),
    question9 = toBvasQuestionAnswers(this.question9),
    date = this.date.toString(),
    sumValue = value
)

fun toStringBvasQuestion(list: List<BvasIndex.QuestionAnswer>) = list.joinToString(separator = ";") { it.toStringValue() }

private fun BvasIndex.QuestionAnswer.toStringValue() = "$title,$value"

private fun toBvasQuestionAnswers(questionAnswersString: String): List<BvasIndex.QuestionAnswer> {
    if (questionAnswersString.isNotBlank()) {
        return questionAnswersString.split(";").map(::toBvasQuestionAnswer)
    }

    return listOf()
}

private fun toBvasQuestionAnswer(questionString: String): BvasIndex.QuestionAnswer {
    val data = questionString.split(",")
    val title = data[0]
    val value = data[1].toInt()

    return BvasIndex.QuestionAnswer(
        title,
        value
    )
}