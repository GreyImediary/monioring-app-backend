package ru.therapyapp.base_api

import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(val text: String)