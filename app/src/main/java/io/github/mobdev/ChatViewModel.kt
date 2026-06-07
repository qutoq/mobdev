package io.github.mobdev

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mobdev.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val repo: ChatRepository, private val session: SessionManager) : ViewModel() {
    var loginState by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf("")

    val channels: Flow<List<String>> = repo.channels

    var selectedChannel by mutableStateOf("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: Flow<List<Message>> = snapshotFlow { selectedChannel }
        .flatMapLatest { channelId ->
            if (channelId.isEmpty()) flowOf(emptyList())
            else repo.getMessages(channelId)
        }

    var isChannelsLoading by mutableStateOf(false)
    var isMessagesLoading by mutableStateOf(false)
    var currentUser by mutableStateOf("")

    init {
        viewModelScope.launch {
            session.username.collect { currentUser = it }
        }
        viewModelScope.launch { repo.retryPendingMessages() }
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
                val api = repo.getApi()
                val response = withContext(Dispatchers.IO) { api.login(LoginRequest(user, pass)) }
                if (response.isSuccessful) {
                    session.saveSession(user, response.body() ?: "")
                    loginState = "success"
                    repo.retryPendingMessages() // Пробуем отправить после логина
                } else {
                    errorMessage = "Ошибка: ${response.code()}"
                    loginState = "error"
                }
            } catch (e: Exception) {
                errorMessage = "Нет интернета"
                loginState = "error"
            }
        }
    }

    fun loadChannels() {
        viewModelScope.launch {
            isChannelsLoading = true
            errorMessage = ""
            try {
                repo.refreshChannels()
            } catch (e: Exception) {
                errorMessage = "Оффлайн режим: данные из кэша"
            } finally {
                isChannelsLoading = false
            }
        }
    }

    fun loadMessages(channelId: String) {
        selectedChannel = channelId
        viewModelScope.launch {
            isMessagesLoading = true
            errorMessage = ""
            repo.retryPendingMessages()
            try {
                repo.refreshMessages(channelId)
            } catch (e: Exception) {
                errorMessage = "Показана история из кэша"
            } finally {
                isMessagesLoading = false
            }
        }
    }

    fun loadMoreMessages(oldestId: String?) {
        if (isMessagesLoading || oldestId == null || oldestId.startsWith("pending")) return
        viewModelScope.launch {
            isMessagesLoading = true
            try {
                repo.refreshMessages(selectedChannel, lastKnownId = oldestId)
            } catch (e: Exception) {
                errorMessage = "Не удалось загрузить старые сообщения"
            } finally { isMessagesLoading = false }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || selectedChannel.isEmpty()) return
        viewModelScope.launch {
            errorMessage = ""
            try {
                repo.sendMessage(selectedChannel, currentUser, text)
            } catch (e: Exception) {
                errorMessage = "Ошибка сети: сообщение будет отправлено позже"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            session.clear()
            loginState = null
            selectedChannel = ""
        }
    }
}