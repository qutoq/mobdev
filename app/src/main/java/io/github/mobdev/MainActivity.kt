package io.github.mobdev

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.room.Room
import io.github.mobdev.api.ChatApi
import io.github.mobdev.db.AppDatabase
import io.github.mobdev.ui.* // Импорт всех экранов
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "chat-db").build()
        val session = SessionManager(this)
        val api = ChatApi.create(session)
        val repo = ChatRepository(api, db.chatDao())

        setContent {
            MaterialTheme {
                val vm: ChatViewModel = viewModel { ChatViewModel(repo, session) }
                val token by session.token.collectAsState("")
                val navController = rememberNavController()
                val isLand = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

                Surface(Modifier.fillMaxSize()) {
                    if (token.isEmpty()) LoginScreen(vm)
                    else if (isLand) {
                        Row {
                            Box(Modifier.weight(1f)) { ChannelsScreen(vm) { vm.loadMessages(it) } }
                            Box(Modifier.weight(2f)) {
                                if (vm.selectedChannel.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Выберите чат") }
                                else MessagesScreen(vm.selectedChannel, vm, false, {}, { navController.navigate("img/${URLEncoder.encode(it, "UTF-8")}") })
                            }
                        }
                    } else {
                        NavHost(navController, "channels") {
                            composable("channels") { ChannelsScreen(vm) { navController.navigate("msg/$it") } }
                            composable("msg/{id}") { MessagesScreen(it.arguments?.getString("id") ?: "", vm, true, { navController.popBackStack() }, { navController.navigate("img/${URLEncoder.encode(it, "UTF-8")}") }) }
                            composable("img/{path}") { ImageViewScreen(it.arguments?.getString("path") ?: "") { navController.popBackStack() } }
                        }
                    }
                }
            }
        }
    }
}