package ru.therapyapp.users.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MKBs : IntIdTable() {
    val code = text("code")
    val name = text("name")
}

class MkbDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MkbDAO>(MKBs)

    var code by MKBs.code
    var name by MKBs.name
}