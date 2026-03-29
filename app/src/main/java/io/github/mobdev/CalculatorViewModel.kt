package io.github.mobdev

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import java.util.Locale

class CalculatorViewModel : ViewModel() {
    var display by mutableStateOf("0")
    var history by mutableStateOf("")

    private var firstNumber: Double? = null
    private var pendingOperation: String? = null
    private var isNewInput = true

    fun onAction(btn: String) {
        when {
            btn in "0123456789" -> {
                if (isNewInput) {
                    if (pendingOperation == null) firstNumber = null
                    display = btn
                } else {
                    display = if (display == "0") btn else display + btn
                }
                isNewInput = false
            }
            btn == "." -> {
                if (isNewInput) {
                    display = "0."
                    isNewInput = false
                } else if (!display.contains(".")) {
                    display += "."
                }
            }
            btn in "+-×÷" -> {
                val current = display.toDoubleOrNull() ?: 0.0

                if (firstNumber == null) {
                    firstNumber = current
                } else if (!isNewInput) {
                    calculateResult()
                    firstNumber = display.toDoubleOrNull()
                }

                pendingOperation = btn
                history = "${format(firstNumber ?: current)} $btn"
                isNewInput = true
            }
            btn == "=" -> {
                if (firstNumber != null && pendingOperation != null) {
                    calculateResult()
                    firstNumber = display.toDoubleOrNull()
                    pendingOperation = null
                    isNewInput = true
                }
            }
            btn == "C" -> {
                display = "0"; history = ""; firstNumber = null
                pendingOperation = null; isNewInput = true
            }
        }
    }

    private fun calculateResult() {
        val first = firstNumber ?: return
        val second = display.toDoubleOrNull() ?: return
        val op = pendingOperation ?: return

        val res = when (op) {
            "+" -> first + second
            "-" -> first - second
            "×" -> first * second
            "÷" -> if (second != 0.0) first / second else Double.NaN
            else -> second
        }

        display = format(res)
        history = ""
        isNewInput = true
    }

    private fun format(d: Double): String {
        if (d.isNaN()) return "Error"
        if (d.isInfinite()) return "Error"
        return if (d == d.toLong().toDouble()) {
            d.toLong().toString()
        } else {
            String.format(Locale.US, "%.8g", d)
                .replace(Regex("0+$"), "")
                .replace(Regex("\\.$"), "")
        }
    }
}