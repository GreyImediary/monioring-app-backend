package ru.therapyapp.doctor_patient_request.model

import kotlinx.serialization.Serializable
import ru.therapyapp.doctor_patient_request.db.DocPatientRequestStatus
import ru.therapyapp.doctor_patient_request.db.DoctorPatientRequestDAO
import ru.therapyapp.users.model.Doctor
import ru.therapyapp.users.model.Patient
import ru.therapyapp.users.model.toDoctor
import ru.therapyapp.users.model.toPatient

@Serializable
data class DoctorPatientRequest(
    val id: Int,
    val doctor: Doctor,
    val patient: Patient,
    val status: DocPatientRequestStatus
)

@Serializable
data class DoctorPatientRequestCreateBody(
    val doctorId: Int,
    val patientId: Int,
    val status: DocPatientRequestStatus,
)

@Serializable
data class DoctorPatientRequestUpdateBody(
    val id: Int,
    val status: DocPatientRequestStatus,
)

fun DoctorPatientRequestDAO.toDoctorPatientRequest() = DoctorPatientRequest(
    id = this.id.value,
    doctor = this.doctorDAO.toDoctor(),
    patient = this.patientDAO.toPatient(),
    status = this.status
)