package ru.therapyapp.users.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Patients : IntIdTable() {
    val user = reference("user", Users)
    val name = varchar("name", 30)
    val surname = varchar("surname", 30)
    val patronymic = varchar("patronymic", 30).nullable()
    val sex = enumerationByName("sex", 6, Sex::class)
    val phoneNumber = varchar("phone_number", 11)
    val additionalPhoneNumber = varchar("additional_phone_number", 11).nullable()
    val email = varchar("email", 100).nullable()
    val birthDate = timestamp("birth_date")
    val patientCardNumber = text("patient_card_number")
}

class PatientDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PatientDAO>(Patients)
    var userDAO by UserDAO referencedOn  Patients.user
    var name by Patients.name
    var surname by Patients.surname
    var patronymic by Patients.patronymic
    var sex by Patients.sex
    var phoneNumber by Patients.phoneNumber
    var additionalPhoneNumber by Patients.additionalPhoneNumber
    var email by Patients.email
    var birthDate by Patients.birthDate
    var patientCardNumber by Patients.patientCardNumber
}