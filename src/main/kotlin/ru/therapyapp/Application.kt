package ru.therapyapp

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.therapyapp.questionnaire.db.Options
import ru.therapyapp.questionnaire.db.Questionnaires
import ru.therapyapp.questionnaire.db.Questions
import ru.therapyapp.questionnaire.routing.questionnaireRouting
import ru.therapyapp.questionnaire_answered.db.QuestionnairesAnswered
import ru.therapyapp.questionnaire_answered.db.QuestionsAnswered
import ru.therapyapp.questionnaire_answered.routing.questionnaireAnsweredRouting
import ru.therapyapp.base_db.DatabaseFactory
import ru.therapyapp.auth.configureAuth
import ru.therapyapp.auth.configureAuthRouting
import ru.therapyapp.comments.db.Comments
import ru.therapyapp.comments.routing.configureCommentRouting
import ru.therapyapp.doctor_patient_request.db.DoctorPatientRequests
import ru.therapyapp.doctor_patient_request.routing.configureDoctorPatientRequestRouting
import ru.therapyapp.index_asdas.db.AsdasIndexes
import ru.therapyapp.index_asdas.routing.configureAsdasRouting
import ru.therapyapp.index_basdai.bd.BasdaiIndexes
import ru.therapyapp.index_basdai.routing.configureBasdaiRouting
import ru.therapyapp.index_bvas.bd.BvasIndexes
import ru.therapyapp.index_bvas.routing.configureBvasRouting
import ru.therapyapp.plugins.configureLogs
import ru.therapyapp.plugins.configureRouting
import ru.therapyapp.plugins.configureSecurity
import ru.therapyapp.plugins.configureSerialization
import ru.therapyapp.users.routings.configureUserRoting
import ru.therapyapp.users.db.Doctors
import ru.therapyapp.users.db.DoctorsPatients
import ru.therapyapp.users.db.Patients
import ru.therapyapp.users.db.Users
import ru.therapyapp.users.routings.configureDoctorRouting
import ru.therapyapp.users.routings.configurePatientRouting

fun main() {

    transaction(DatabaseFactory.database) {
        SchemaUtils.create(
            Users,
            Patients,
            Doctors,
            DoctorsPatients,
            DoctorPatientRequests,
            BasdaiIndexes,
            BvasIndexes,
            AsdasIndexes,
            Comments,
            Questionnaires,
            Questions,
            Options,
            QuestionnairesAnswered,
            QuestionsAnswered
        )
    }
    embeddedServer(Netty, port = 8080) {
        configureAuth()
        configureAuthRouting()
        configureLogs()
        configureUserRoting()
        configureSerialization()
        configureSecurity()
        configureRouting()
        configureDoctorRouting()
        configurePatientRouting()
        configureDoctorPatientRequestRouting()
        configureBasdaiRouting()
        configureBvasRouting()
        configureAsdasRouting()
        configureCommentRouting()
        questionnaireRouting()
        questionnaireAnsweredRouting()
    }.start(wait = true)
}
