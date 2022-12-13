package ru.therapyapp.questionnaire.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Questions : IntIdTable() {
    val questionnaire = reference("quesionnaire_id", Questionnaires)
    val title = text("title")
    val type = enumerationByName("type", 15, QuestionType::class)
}

class QuestionDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QuestionDAO>(Questions)
    var questionnaireDAO by QuestionnaireDAO referencedOn Questions.questionnaire
    var title by Questions.title
    var type by Questions.type
    val options by OptionDAO referrersOn Options.question
}

enum class QuestionType {
    FIELD, CHECKBOXES, RADIOBUTTONS
}