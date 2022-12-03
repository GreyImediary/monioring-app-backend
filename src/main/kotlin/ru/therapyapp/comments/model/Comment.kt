package ru.therapyapp.comments.model

import kotlinx.serialization.Serializable
import ru.therapyapp.comments.db.CommentDAO
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toPatient

@Serializable
data class Comment(
    val patient: Patient,
    val comment: String,
)

@Serializable
data class CommentRequestBody(
    val patientId: Int,
    val comment: String,
)

fun CommentDAO.toComment() = Comment(
    patient = patientDAO.toPatient(),
    comment = comment
)

