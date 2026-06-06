package io.github.mobdev

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mobdev.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val api: ChatApi, private val session: SessionManager) : ViewModel() {
    var loginState by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf("")
    var channels by mutableStateOf<List<String>>(emptyList())
    var messages by mutableStateOf<List<Message>>(emptyList())
    var isChannelsLoading by mutableStateOf(false)
    var isMessagesLoading by mutableStateOf(false)
    var currentUser by mutableStateOf("")
    var selectedChannel by mutableStateOf("")

    init {
        viewModelScope.launch { session.username.collect { currentUser = it } }
    }

    fun login(user: String, pass: String) {
        if (user.isEmpty() || pass.isEmpty()) {
            errorMessage = "Заполните все поля"
            return
        }

        viewModelScope.launch {
            loginState = "loading"
            errorMessage = ""
            try {
                val response = withContext(Dispatchers.IO) { api.login(LoginRequest(user, pass)) }

                if (response.isSuccessful) {
                    val token = response.body() ?: ""
                    session.saveSession(user, token)
                    loginState = "success"
                } else {
                    if (response.code() == 401) {
                        errorMessage = "Неверный логин или пароль"
                    } else {
                        errorMessage = "Ошибка сервера: ${response.code()}"
                    }
                    loginState = "error"
                }
            } catch (e: Exception) {
                errorMessage = "Проверьте соединение с интернетом"
                loginState = "error"
            }
        }
    }

    fun loadChannels() {
        viewModelScope.launch {
            isChannelsLoading = true
            try { channels = withContext(Dispatchers.IO) { api.getChannels() } }
            catch (e: Exception) { errorMessage = "Ошибка каналов" }
            finally { isChannelsLoading = false }
        }
    }

    fun loadMessages(channelId: String) {
        selectedChannel = channelId
        viewModelScope.launch {
            isMessagesLoading = true
            messages = emptyList()
            try {
                // Загружаем последние 20
                val res = withContext(Dispatchers.IO) {
                    api.getMessages(channelId, limit = 20, lastKnownId = "999999999", reverse = true)
                }
                messages = res.reversed()
            } catch (e: Exception) { errorMessage = "Ошибка сообщений" }
            finally { isMessagesLoading = false }
        }
    }

    fun loadMoreMessages() {
        if (isMessagesLoading || messages.isEmpty()) return
        viewModelScope.launch {
            isMessagesLoading = true
            try {
                val oldestId = messages.first().id
                val res = withContext(Dispatchers.IO) {
                    api.getMessages(selectedChannel, limit = 20, lastKnownId = oldestId, reverse = true)
                }
                if (res.isNotEmpty()) messages = res.reversed() + messages
            } finally { isMessagesLoading = false }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || selectedChannel.isEmpty()) return
        viewModelScope.launch {
            try {
                val msg = Message(from = currentUser, to = selectedChannel, data = MessageData(Text = TextData(text)))
                withContext(Dispatchers.IO) { api.sendMessage(msg) }
                loadMessages(selectedChannel)
            } catch (e: Exception) { errorMessage = "Ошибка отправки" }
        }
    }

    fun logout() { viewModelScope.launch { session.clear(); loginState = null; messages = emptyList() } }
}