package ru.therapyapp.comments.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object Comments : IntIdTable() {
    val patient = reference("patient_id", Patients)
    val comment = text("comment")
}

class CommentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CommentDAO>(Comments)

    var patientDAO by PatientDAO referencedOn Comments.patient
    var comment by Comments.comment
}