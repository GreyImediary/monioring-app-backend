package questionnaire_answered.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.therapyapp.users.db.PatientDAO
import ru.therapyapp.users.db.Patients

object QuestionnairesAnswered : IntIdTable() {
    val name = text("name")
    val patient = reference("patient_id", Patients)
    val date = timestamp("date")
}

class QuestionnaireAnsweredDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QuestionnaireAnsweredDAO>(QuestionnairesAnswered)

    var name by QuestionnairesAnswered.name
    var patientDAO by PatientDAO referencedOn QuestionnairesAnswered.patient
    var date by QuestionnairesAnswered.date
    val answeredQuestions by QuestionAnsweredDAO referrersOn QuestionsAnswered.questionnaireAnsweredId
}