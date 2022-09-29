package ru.therapyapp.users.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Doctors : IntIdTable() {
    val user = reference("user_id", Users)
    val name = varchar("name", 30)
    val surname = varchar("surname", 30)
    val patronymic = varchar("patronymic", 30).nullable()
    val sex = enumerationByName("sex", 6, Sex::class)
    val phoneNumber = varchar("phone_number", 11)
    val email = varchar("email", 100)
}

class DoctorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DoctorDAO>(Doctors)
    var userDAO by UserDAO referencedOn Doctors.user
    var name by Doctors.name
    var surname by Doctors.surname
    var patronymic by Doctors.patronymic
    var sex by Doctors.sex
    var phoneNumber by Doctors.phoneNumber
    var email by Doctors.email
    var patients by PatientDAO via DoctorsPatients
}
