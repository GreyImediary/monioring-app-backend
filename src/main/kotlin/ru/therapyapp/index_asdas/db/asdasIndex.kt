package ru.therapyapp.index_asdas.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object AsdasIndexes : IntIdTable() {
    val patient = reference("patient_id", Patients)
    val question1 = integer("question_1")
    val question2 = integer("question_2")
    val question3 = integer("question_3")
    val question4 = integer("question_4")
    val srbSoeType = enumerationByName("srb_seo", 3, SrbSoeType::class)
    val srbSoeValue = double("srb_soe_value")
    val date = timestamp("date")
    val value = double("value")

}

enum class SrbSoeType() {
    SRB, SOE
}

class AsdasIndexDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AsdasIndexDao>(AsdasIndexes)

    var patientDAO by PatientDAO referencedOn AsdasIndexes.patient
    var question1 by AsdasIndexes.question1
    var question2 by AsdasIndexes.question2
    var question3 by AsdasIndexes.question3
    var question4 by AsdasIndexes.question4
    var srbSoeType by AsdasIndexes.srbSoeType
    var srbSoeValue by AsdasIndexes.srbSoeValue
    var date by AsdasIndexes.date
    var value by AsdasIndexes.value
}