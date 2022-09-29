package ru.therapyapp.users.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val login = text("login").uniqueIndex()
    val password = text("password")
    val userType = enumerationByName("user_type", 7, UserType::class)
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(Users)
    var login by Users.login
    var password by Users.password
    var userType by Users.userType
}