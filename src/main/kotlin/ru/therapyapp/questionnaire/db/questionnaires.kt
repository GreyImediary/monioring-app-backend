package ru.therapyapp.questionnaire.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.Doctors
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object Questionnaires : IntIdTable() {
    val name = text("name")
    val byDoctor = reference("doctor_id", Doctors)
    val forPatient = reference("user_id", Patients).nullable()
}

class QuestionnaireDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QuestionnaireDAO>(Questionnaires)
    var name by Questionnaires.name
    var byDoctor by DoctorDAO referencedOn Questionnaires.byDoctor
    var forPatient by PatientDAO optionalReferencedOn Questionnaires.forPatient
    val questions by QuestionDAO referrersOn Questions.questionnaire
}