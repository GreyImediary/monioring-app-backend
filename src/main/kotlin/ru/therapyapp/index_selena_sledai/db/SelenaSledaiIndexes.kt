package ru.therapyapp.index_selena_sledai.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object SelenaSledaiIndexes : IntIdTable() {
    val patient = reference("patient_id", Patients)
    val answer = text("answer")
    val date = timestamp("date")
    val value = integer("value")
}

class SelenaSledaiIndexesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SelenaSledaiIndexesDAO>(SelenaSledaiIndexes)

    var patientDAO by PatientDAO referencedOn SelenaSledaiIndexes.patient
    var answer by SelenaSledaiIndexes.answer
    var date by SelenaSledaiIndexes.date
    var value by SelenaSledaiIndexes.value
}