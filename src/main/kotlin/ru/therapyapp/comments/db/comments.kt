package ru.therapyapp.comments.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.Doctors
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object Comments : IntIdTable() {
    val patient = reference("patient_id", Patients)
    val doctor = reference("doctor_id", Doctors)
    val comment = text("comment")
    val date = timestamp("date")
}

class CommentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CommentDAO>(Comments)
    var patientDAO by PatientDAO referencedOn Comments.patient
    var doctorDAO by DoctorDAO referencedOn Comments.doctor
    var comment by Comments.comment
    var date by Comments.date
}