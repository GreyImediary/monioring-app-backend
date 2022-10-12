package ru.therapyapp.index.model

import kotlinx.serialization.Serializable
import ru.therapyapp.index.db.IndexDAO
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toPatient

@Serializable
data class Index(
    val patient: Patient,
    val indexName: String,
    val indexValue: Float,
    val date: String,
)

@Serializable
data class IndexCreationBody(
    val patientId: Int,
    val indexName: String,
    val indexValue: Float,
    val date: String
)

fun IndexDAO.toIndex() = Index(
    patient = this.patient.toPatient(),
    indexName = this.indexName,
    indexValue = this.indexValue,
    date = this.date.toString()
)

