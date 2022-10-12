package ru.therapyapp.doctor_patient_request.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.therapyapp.users.db.DoctorDAO
import ru.therapyapp.users.db.Doctors
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object DoctorPatientRequests : IntIdTable() {
    val doctor = reference("doctor_id", Doctors)
    val patient = reference("patient_id", Patients)
    val status = enumerationByName("request_status", 8, DocPatientRequestStatus::class)
}

class DoctorPatientRequestDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DoctorPatientRequestDAO>(DoctorPatientRequests)
    var doctorDAO by DoctorDAO referencedOn DoctorPatientRequests.doctor
    var patientDAO by PatientDAO referencedOn DoctorPatientRequests.patient
    var status by DoctorPatientRequests.status
}

enum class DocPatientRequestStatus {
    ACCEPTED,
    DECLINED,
    PENDING,
}