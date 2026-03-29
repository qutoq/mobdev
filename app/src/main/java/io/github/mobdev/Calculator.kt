package io.github.mobdev

import android.os.Bundle

class Calculator {
    private var display = "0"
    private var firstNumber = 0.0
    private var secondNumber = 0.0
    private var operation = ""
    private var isNewNumber = true

    fun onNumberClick(number: String) {
        if (isNewNumber) {
            display = number
            isNewNumber = false
        } else {
            if (display == "0") {
                display = number
            } else {
                display += number
            }
        }
    }

    fun onDotClick() {
        if (isNewNumber) {
            display = "0."
            isNewNumber = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    fun onOperationClick(op: String) {
        if (operation.isNotEmpty() && !isNewNumber) {
            secondNumber = display.toDoubleOrNull() ?: 0.0
            val result = calculate(firstNumber, secondNumber, operation)
            display = formatResult(result)
            firstNumber = result
        } else {
            firstNumber = display.toDoubleOrNull() ?: 0.0
        }

        operation = op
        isNewNumber = true
    }

    fun onEqualsClick(): String {
        secondNumber = display.toDoubleOrNull() ?: 0.0
        val result = calculate(firstNumber, secondNumber, operation)
        display = formatResult(result)
        isNewNumber = true
        operation = ""
        return display
    }

    private fun calculate(first: Double, second: Double, op: String): Double {
        return when (op) {
            "+" -> first + second
            "-" -> first - second
            "×" -> first * second
            "÷" -> if (second != 0.0) first / second else 0.0
            else -> first
        }
    }

    private fun formatResult(result: Double): String {
        return when {
            result == result.toLong().toDouble() -> result.toLong().toString()
            else -> String.format("%.2f", result)
        }
    }

    fun onClear() {
        display = "0"
        firstNumber = 0.0
        secondNumber = 0.0
        operation = ""
        isNewNumber = true
    }

    fun getDisplay(): String = display

    fun getSecondaryDisplay(): String {
        return if (operation.isNotEmpty()) {
            String.format("%.0f %s", firstNumber, operation)
        } else {
            ""
        }
    }

    fun save(bundle: Bundle) {
        bundle.putString("display", display)
        bundle.putDouble("firstNumber", firstNumber)
        bundle.putDouble("secondNumber", secondNumber)
        bundle.putString("operation", operation)
        bundle.putBoolean("isNewNumber", isNewNumber)
    }

    fun restore(bundle: Bundle) {
        display = bundle.getString("display", "0") ?: "0"
        firstNumber = bundle.getDouble("firstNumber", 0.0)
        secondNumber = bundle.getDouble("secondNumber", 0.0)
        operation = bundle.getString("operation", "") ?: ""
        isNewNumber = bundle.getBoolean("isNewNumber", true)
    }
}