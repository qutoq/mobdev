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
import io.github.mobdev.api.ChatApi
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val session = SessionManager(this)
        val api = ChatApi.create(session)

        setContent {
            MaterialTheme {
                val vm: ChatViewModel = viewModel { ChatViewModel(api, session) }
                val token by session.token.collectAsState("")
                val navController = rememberNavController()
                val isLand = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (token.isEmpty()) {
                        LoginScreen(vm)
                    } else if (isLand) {
                        // ЛАНДШАФТ (Master-Detail)
                        Row(Modifier.fillMaxSize()) {
                            Box(Modifier.weight(1f)) { ChannelsScreen(vm) { vm.loadMessages(it) } }
                            Box(Modifier.weight(2f)) {
                                if (vm.selectedChannel.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Выберите чат") }
                                } else {
                                    MessagesScreen(vm.selectedChannel, vm, false, {}, {
                                        val enc = URLEncoder.encode(it, "UTF-8")
                                        navController.navigate("img/$enc")
                                    })
                                }
                            }
                        }
                    } else {
                        // ПОРТРЕТ (Обычная навигация)
                        NavHost(navController, startDestination = "channels") {
                            composable("channels") { ChannelsScreen(vm) { navController.navigate("msg/$it") } }
                            composable("msg/{id}") {
                                MessagesScreen(it.arguments?.getString("id") ?: "", vm, true, { navController.popBackStack() }, { path ->
                                    val enc = URLEncoder.encode(path, "UTF-8")
                                    navController.navigate("img/$enc")
                                })
                            }
                            composable("img/{path}") {
                                ImageViewScreen(it.arguments?.getString("path") ?: "") { navController.popBackStack() }
                            }
                        }
                    }
                }
            }
        }
    }
}