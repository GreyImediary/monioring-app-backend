package ru.therapyapp.index_bvas.bd

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object BvasIndexes : IntIdTable() {
    val patient = reference("patient_id", Patients)
    val question1 = text("question_1")
    val question2 = text("question_2")
    val question3 = text("question_3")
    val question4 = text("question_4")
    val question5 = text("question_5")
    val question6 = text("question_6")
    val question7 = text("question_7")
    val question8 = text("question_8")
    val question9 = text("question_9")
    val date = timestamp("date")
    val value = integer("value")
}

class BvasIndexDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BvasIndexDAO>(BvasIndexes)

    var patientDAO by PatientDAO referencedOn BvasIndexes.patient
    var question1 by BvasIndexes.question1
    var question2 by BvasIndexes.question2
    var question3 by BvasIndexes.question3
    var question4 by BvasIndexes.question4
    var question5 by BvasIndexes.question5
    var question6 by BvasIndexes.question6
    var question7 by BvasIndexes.question7
    var question8 by BvasIndexes.question8
    var question9 by BvasIndexes.question9
    var date by BvasIndexes.date
    var value by BvasIndexes.value
}