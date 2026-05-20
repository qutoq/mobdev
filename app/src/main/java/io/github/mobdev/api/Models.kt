package io.github.mobdev.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val name: String, val pwd: String)

@Serializable
data class Message(
    val id: String? = null,
    val from: String,
    val to: String? = "1@channel",
    val data: MessageData,
    val time: Long? = null
)

@Serializable
data class MessageData(
    val Text: TextData? = null,
    val Image: ImageData? = null
)

@Serializable
data class TextData(val text: String)

@Serializable
data class ImageData(val link: String)

@Serializable
data class SendResponse(val id: String)