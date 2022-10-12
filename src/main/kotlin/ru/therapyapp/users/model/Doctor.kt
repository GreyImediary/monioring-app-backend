package ru.therapyapp.users.model

import kotlinx.serialization.Serializable
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.Sex

@Serializable
data class Doctor(
    val id: Int,
    val userId: Int,
    val name: String,
    val surname: String,
    val patronymic: String?,
    val sex: Sex,
    val phoneNumber: String,
    val email: String?,
    val patients: List<Patient>
)

@Serializable
data class DoctorRequestBody(
    val userId: Int,
    val name: String,
    val surname: String,
    val patronymic: String?,
    val sex: Sex,
    val phoneNumber: String,
    val email: String?,
)

fun DoctorDAO.toDoctor() = Doctor(
    id = this.id.value,
    userId = this.userDAO.id.value,
    name = this.name,
    surname = this.surname,
    patronymic = this.patronymic,
    sex = this.sex,
    phoneNumber = this.phoneNumber,
    email = this.email,
    patients = patients.map { it.toPatient() }
)