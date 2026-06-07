@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package io.github.mobdev.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Message(
    val id: String? = null,
    val from: String,
    val to: String? = "1@channel",
    val data: MessageData,
    val time: Long? = null,
    @Transient val isPending: Boolean = false
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
data class LoginRequest(val name: String, val pwd: String)