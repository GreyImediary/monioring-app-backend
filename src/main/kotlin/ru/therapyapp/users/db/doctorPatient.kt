package ru.therapyapp.users.db

import org.jetbrains.exposed.sql.Table


object DoctorsPatients : Table() {
    val doctor = reference("doctor_id", Doctors)
    val patient = reference("patient_id", Patients)

    override val primaryKey: PrimaryKey = PrimaryKey(doctor, patient, name = "PK_DoctorsPatients_act")
}