package ru.therapyapp.index_asdas.model

import kotlinx.serialization.Serializable
import ru.therapyapp.index_asdas.db.AsdasIndexDao
import ru.therapyapp.index_asdas.db.SrbSoeType
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toPatient

@Serializable
data class AsdasIndex(
    val patient: Patient,
    val question1: Int,
    val question2: Int,
    val question3: Int,
    val question4: Int,
    val srbSoeType: SrbSoeType,
    val srbSoeValue: Double,
    val date: String,
    val sumValue: Double
)

@Serializable
data class AsdasIndexRequestBody(
    val patientId: Int,
    val question1: Int,
    val question2: Int,
    val question3: Int,
    val question4: Int,
    val srbSoeType: SrbSoeType,
    val srbSoeValue: Double,
    val date: String,
    val sumValue: Double
)

fun AsdasIndexDao.toAsdasIndex() = AsdasIndex(
    patient = this.patientDAO.toPatient(),
    question1 = this.question1,
    question2 = this.question2,
    question3 = this.question3,
    question4 = this.question4,
    srbSoeType = this.srbSoeType,
    srbSoeValue = this.srbSoeValue,
    date = this.date.toString(),
    sumValue = this.value
)