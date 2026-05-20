package io.github.mobdev

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_KEY = stringPreferencesKey("username")
    }

    val token: Flow<String> = context.dataStore.data.map { it[TOKEN_KEY] ?: "" }
    val username: Flow<String> = context.dataStore.data.map { it[USER_KEY] ?: "" }

    suspend fun saveSession(user: String, token: String) {
        context.dataStore.edit { it[USER_KEY] = user; it[TOKEN_KEY] = token }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}