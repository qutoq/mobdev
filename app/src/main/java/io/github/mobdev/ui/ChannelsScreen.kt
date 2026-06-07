package io.github.mobdev

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsScreen(vm: ChatViewModel, onChannelClick: (String) -> Unit) {
    val channels by vm.channels.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        vm.loadChannels()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Каналы") },
                actions = {
                    Button(onClick = { vm.logout() }) {
                        Text("Выход")
                    }
                }
            )
        }
    ) { padding ->
        if (vm.isChannelsLoading && channels.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(channels) { channel ->
                    ListItem(
                        headlineContent = { Text(channel) },
                        modifier = Modifier.clickable { onChannelClick(channel) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}