package io.github.mobdev

import io.github.mobdev.api.*
import io.github.mobdev.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ChatRepository(private val api: ChatApi, private val dao: ChatDao) {
    val channels: Flow<List<String>> = dao.getAllChannels().map { it.map { e -> e.name } }

    suspend fun refreshChannels() {
        val remote = api.getChannels()
        dao.insertChannels(remote.map { ChannelEntity(it) })
    }

    fun getMessages(channelId: String): Flow<List<Message>> =
        dao.getMessagesForChannel(channelId).map { it.map { e -> e.toModel() } }

    suspend fun refreshMessages(channelId: String, lastKnownId: String? = "999999999") {
        val remote = api.getMessages(channelId, limit = 40, lastKnownId = lastKnownId, reverse = true)
        dao.insertMessages(remote.map { it.toEntity(channelId) })
    }

    suspend fun sendMessage(channelId: String, from: String, text: String) {
        val tempId = "pending_${UUID.randomUUID()}"
        dao.insertMessages(listOf(MessageEntity(tempId, channelId, from, text, null, System.currentTimeMillis(), true)))
        try {
            api.sendMessage(Message(from = from, to = channelId, data = MessageData(Text = TextData(text))))
            dao.deleteMessageById(tempId)
            refreshMessages(channelId)
        } catch (e: Exception) { throw e }
    }

    suspend fun retryPendingMessages() {
        val pending = dao.getPendingMessages()
        for (entity in pending) {
            try {
                api.sendMessage(Message(from = entity.fromUser, to = entity.channelId, data = MessageData(Text = entity.text?.let { TextData(it) })))
                dao.deleteMessageById(entity.id)
            } catch (e: Exception) { /* skip */ }
        }
    }

    suspend fun getApi() = api

    private fun MessageEntity.toModel() = Message(id, fromUser, channelId, MessageData(text?.let { TextData(it) }, imageLink?.let { ImageData(it) }), timestamp, isPending)
    private fun Message.toEntity(cId: String) = MessageEntity(id ?: UUID.randomUUID().toString(), cId, from, data.Text?.text, data.Image?.link, time ?: System.currentTimeMillis(), false)
}