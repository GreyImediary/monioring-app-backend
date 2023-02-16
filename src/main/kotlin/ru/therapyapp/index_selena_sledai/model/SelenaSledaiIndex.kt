package ru.therapyapp.index_selena_sledai.model

import kotlinx.serialization.Serializable
import ru.therapyapp.index_selena_sledai.db.SelenaSledaiIndexesDAO
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toPatient

@Serializable
data class SelenaSledaiIndex(
    val patient: Patient,
    val answer: String,
    val date: String,
    val sumValue: Int,
)

@Serializable
data class SelenaSledaiIndexBody(
    val patientId: Int,
    val answer: String,
    val date: String,
    val sumValue: Int,
)

fun SelenaSledaiIndexesDAO.toSelenaSledai() = SelenaSledaiIndex(
    patient = patientDAO.toPatient(),
    answer = answer,
    date = date.toString(),
    sumValue = value
)