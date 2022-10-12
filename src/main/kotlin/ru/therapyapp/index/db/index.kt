package ru.therapyapp.index.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object Indexes : IntIdTable() {
    val patient = reference("patient_id", Patients)
    val indexName = varchar("index_name", 30)
    val indexValue = float("value")
    val date = timestamp("date")
}

class IndexDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<IndexDAO>(Indexes)

    var patient by PatientDAO referencedOn Indexes.patient
    var indexName by Indexes.indexName
    var indexValue by Indexes.indexValue
    var date by Indexes.date
}