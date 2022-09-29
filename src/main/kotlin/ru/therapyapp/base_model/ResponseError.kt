package ru.therapyapp.base_model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(val text: String)