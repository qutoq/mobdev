package io.github.mobdev

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.net.URLDecoder

@Composable
fun ImageViewScreen(path: String, onBack: () -> Unit) {
    val decoded = URLDecoder.decode(path, "UTF-8")
    Box(Modifier.fillMaxSize().background(Color.Black).clickable { onBack() }) {
        AsyncImage(
            model = "https://faerytea.name/img/$decoded",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}