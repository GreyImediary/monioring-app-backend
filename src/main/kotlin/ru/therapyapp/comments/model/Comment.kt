package ru.therapyapp.comments.model

import kotlinx.serialization.Serializable
import ru.therapyapp.comments.db.CommentDAO
import ru.therapyapp.users.model.Doctor
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toDoctor
import ru.therapyapp.users.model.toPatient

@Serializable
data class Comment(
    val patient: Patient,
    val doctor: Doctor,
    val comment: String,
    val date: String
)

@Serializable
data class CommentRequestBody(
    val patientId: Int,
    val doctorId: Int,
    val comment: String,
    val date: String,
)

fun CommentDAO.toComment() = Comment(
    patient = patientDAO.toPatient(),
    doctor = doctorDAO.toDoctor(),
    comment = comment,
    date = date.toString()
)

