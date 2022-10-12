package ru.therapyapp.users.model

import kotlinx.serialization.Serializable
import ru.therapyapp.users.db.UserDAO
import ru.therapyapp.users.db.UserType

@Serializable
data class User(
    val id: Int,
    val login: String,
    val userType: UserType
)

@Serializable
data class UserCreationBody(
    val login: String,
    val password: String,
    val userType: UserType
)

fun UserDAO.toUser() = User(
    id = this.id.value,
    login = this.login,
    userType = this.userType
)