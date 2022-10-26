package ru.therapyapp.index_basdai.bd

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object BasdaiIndexes : IntIdTable() {
    val patient = reference("patient_id", Patients)
    val question1 = integer("question_1")
    val question2 = integer("question_2")
    val question3 = integer("question_3")
    val question4 = integer("question_4")
    val question5 = integer("question_5")
    val question6 = integer("question_6")
    val date = timestamp("date")
    val value = double("value")
}

class BasdaiIndexDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BasdaiIndexDAO>(BasdaiIndexes)

    var patientDAO by PatientDAO referencedOn BasdaiIndexes.patient
    var question1 by BasdaiIndexes.question1
    var question2 by BasdaiIndexes.question2
    var question3 by BasdaiIndexes.question3
    var question4 by BasdaiIndexes.question4
    var question5 by BasdaiIndexes.question5
    var question6 by BasdaiIndexes.question6
    var date by BasdaiIndexes.date
    var value by BasdaiIndexes.value
}