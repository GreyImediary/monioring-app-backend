package ru.therapyapp.users.model

import kotlinx.serialization.Serializable
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.MkbDAO
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Sex

@Serializable
data class Patient(
    val id: Int,
    val name: String,
    val surname: String,
    val patronymic: String?,
    val sex: Sex,
    val phoneNumber: String,
    val additionalPhoneNumber: String?,
    val email: String?,
    val birthDate: String,
    val patientCardNumber: String,
    val mkb: Mkb,
)

@Serializable
data class Mkb(
    val name: String,
    val code: String
)

@Serializable
data class PatientBodyRequest(
    val name: String,
    val surname: String,
    val patronymic: String?,
    val sex: Sex,
    val phoneNumber: String,
    val additionalPhoneNumber: String?,
    val email: String?,
    val birthDate: String,
    val patientCardNumber: String,
    val mkb: Mkb,
)

fun PatientDAO.toPatient() = Patient(
    id = this.id.value,
    name = this.name,
    surname = this.surname,
    patronymic = this.patronymic,
    sex = this.sex,
    phoneNumber = this.phoneNumber,
    additionalPhoneNumber = this.additionalPhoneNumber,
    email = this.email,
    birthDate = this.birthDate.toString(),
    patientCardNumber = this.patientCardNumber,
    mkb = mkbDAO.toMkb()
)

fun MkbDAO.toMkb() = Mkb(
    name = this.name,
    code = this.code
)