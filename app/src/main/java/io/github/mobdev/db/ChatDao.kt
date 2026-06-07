package io.github.mobdev.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM channels")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY timestamp ASC")
    fun getMessagesForChannel(channelId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("SELECT * FROM messages WHERE isPending = 1")
    suspend fun getPendingMessages(): List<MessageEntity>
}