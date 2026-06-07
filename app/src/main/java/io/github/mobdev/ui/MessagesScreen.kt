package io.github.mobdev.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.mobdev.ChatViewModel
import io.github.mobdev.api.Message
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(channelId: String, vm: ChatViewModel, showBack: Boolean, onBack: () -> Unit, onImageClick: (String) -> Unit) {
    val messages by vm.messages.collectAsState(initial = emptyList())
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(channelId) { vm.loadMessages(channelId) }
    LaunchedEffect(messages.size) { if(messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(channelId) }, navigationIcon = { if(showBack) IconButton(onClick = onBack) { Text("←") } }) },
        bottomBar = {
            BottomAppBar(contentPadding = PaddingValues(8.dp)) {
                TextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f))
                IconButton(onClick = { vm.sendMessage(text); text = "" }, enabled = text.isNotBlank()) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }
        }
    ) { p ->
        Box(Modifier.padding(p)) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                item { TextButton(onClick = { vm.loadMoreMessages(messages.firstOrNull()?.id) }, modifier = Modifier.fillMaxWidth()) { Text("Загрузить старые") } }
                items(messages) { MessageItem(it, onImageClick) }
            }
            if (vm.isMessagesLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
            if (vm.errorMessage.isNotEmpty()) Text(vm.errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp))
        }
    }
}

@Composable
fun MessageItem(msg: Message, onImageClick: (String) -> Unit) {
    Card(Modifier.padding(8.dp).fillMaxWidth().graphicsLayer(alpha = if(msg.isPending) 0.5f else 1f)) {
        Column(Modifier.padding(12.dp)) {
            Row {
                Text(msg.from, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                if(msg.isPending) Text(" ⏳", style = MaterialTheme.typography.labelSmall)
            }
            msg.data.Text?.let { Text(it.text) }
            msg.data.Image?.let { img ->
                AsyncImage(model = "https://faerytea.name/thumb/${img.link}", contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clickable { onImageClick(img.link) })
            }
            msg.time?.let { Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(it), modifier = Modifier.align(Alignment.End), style = MaterialTheme.typography.labelSmall) }
        }
    }
}