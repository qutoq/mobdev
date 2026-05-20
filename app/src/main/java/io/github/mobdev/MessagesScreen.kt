package io.github.mobdev

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.mobdev.api.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(channelId: String, vm: ChatViewModel, showBack: Boolean, onBack: () -> Unit, onImageClick: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(channelId) { vm.loadMessages(channelId) }
    LaunchedEffect(vm.messages.size) { if (vm.messages.isNotEmpty()) listState.animateScrollToItem(vm.messages.size) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(channelId) }, navigationIcon = {
                if (showBack) IconButton(onClick = onBack) { Text("←") }
            })
        },
        bottomBar = {
            BottomAppBar(contentPadding = PaddingValues(8.dp)) {
                TextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f), placeholder = { Text("Сообщение...") })
                IconButton(onClick = { vm.sendMessage(text); text = "" }, enabled = text.isNotBlank()) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
    ) { p ->
        Box(Modifier.padding(p).fillMaxSize()) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                item {
                    TextButton(onClick = { vm.loadMoreMessages() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Загрузить старые сообщения")
                    }
                }
                items(vm.messages) { MessageItem(it, onImageClick) }
            }
            if (vm.isMessagesLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun MessageItem(msg: Message, onImageClick: (String) -> Unit) {
    Card(Modifier.padding(8.dp).fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(msg.from, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            msg.data.Text?.let { Text(it.text) }
            msg.data.Image?.let { img ->
                AsyncImage(
                    model = "https://faerytea.name/thumb/${img.link}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp).clickable { onImageClick(img.link) }
                )
            }
        }
    }
}