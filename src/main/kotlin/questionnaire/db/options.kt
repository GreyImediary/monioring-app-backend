package questionnaire.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Options : IntIdTable() {
    val question = reference("question_id", Questions)
    val description = text("description")
}

class OptionDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OptionDAO>(Options)

    var questionDAO by QuestionDAO referencedOn Options.question
    var description by Options.description
}