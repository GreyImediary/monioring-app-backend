package questionnaire_answered.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object QuestionsAnswered : IntIdTable() {
    val questionnaireAnsweredId = reference("questionnaire_answered_id", QuestionnairesAnswered)
    val title = text("title")
    val answer = text("answer")
}

class QuestionAnsweredDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QuestionAnsweredDAO>(QuestionsAnswered)
    var questionnaireAnsweredDAO by QuestionnaireAnsweredDAO referencedOn QuestionsAnswered.questionnaireAnsweredId
    var title by QuestionsAnswered.title
    var answer by QuestionsAnswered.answer
}