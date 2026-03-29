package io.github.mobdev

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    val vm: CalculatorViewModel = viewModel()
                    val config = LocalConfiguration.current

                    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp), contentAlignment = Alignment.CenterEnd) {
                                CalculatorDisplay(vm)
                            }
                            Box(modifier = Modifier.weight(1.2f).fillMaxHeight().padding(8.dp)) {
                                CalculatorButtons(vm, isLandscape = true)
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp), contentAlignment = Alignment.BottomEnd) {
                                CalculatorDisplay(vm)
                            }
                            Box(modifier = Modifier.fillMaxWidth().weight(2.8f).padding(8.dp)) {
                                CalculatorButtons(vm, isLandscape = false)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorDisplay(vm: CalculatorViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = vm.history, color = Color(0xFFFF9800), fontSize = 22.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        Text(text = vm.display, color = Color.White, fontSize = 60.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, maxLines = 2)
    }
}

@Composable
fun CalculatorButtons(vm: CalculatorViewModel, isLandscape: Boolean) {
    val rows = listOf(
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "=", "+"),
        listOf("C")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { char ->
                    Button(
                        onClick = { vm.onAction(char) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = when(char) {
                            "C" -> Color(0xFFB00020); "=" -> Color(0xFFFF9800)
                            in "+-×÷" -> Color(0xFF424242); else -> Color(0xFF333333)
                        }),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = char, fontSize = if (isLandscape) 22.sp else 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}