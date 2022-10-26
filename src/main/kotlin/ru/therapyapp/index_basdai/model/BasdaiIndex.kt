package ru.therapyapp.index_basdai.model

import kotlinx.serialization.Serializable
import ru.therapyapp.index_basdai.bd.BasdaiIndexDAO
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toPatient

@Serializable
data class BasdaiIndex(
    val patient: Patient,
    val question1Value: Int,
    val question2Value: Int,
    val question3Value: Int,
    val question4Value: Int,
    val question5Value: Int,
    val question6Value: Int,
    val date: String,
    val sumValue: Double,
)

@Serializable
data class BasdaiIndexRequestBody(
    val patientId: Int,
    val question1Value: Int,
    val question2Value: Int,
    val question3Value: Int,
    val question4Value: Int,
    val question5Value: Int,
    val question6Value: Int,
    val date: String,
    val sumValue: Double,
)

fun BasdaiIndexDAO.toBasdaiIndex() = BasdaiIndex(
    patient = this.patientDAO.toPatient(),
    question1Value = this.question1,
    question2Value = this.question2,
    question3Value = this.question3,
    question4Value = this.question4,
    question5Value = this.question5,
    question6Value = this.question6,
    date = this.date.toString(),
    sumValue = this.value
)