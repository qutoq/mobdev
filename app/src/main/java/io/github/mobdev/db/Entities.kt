package io.github.mobdev.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(@PrimaryKey val name: String)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val channelId: String,
    val fromUser: String,
    val text: String?,
    val imageLink: String?,
    val timestamp: Long,
    val isPending: Boolean = false
)