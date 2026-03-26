package io.github.mobdev

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private val calculator = Calculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)

        if (savedInstanceState != null) {
            calculator.restore(savedInstanceState)
            updateDisplay()
        }

        setupButtons()
    }

    private fun setupButtons() {
        // Цифры
        mapOf(
            R.id.btn_0 to "0",
            R.id.btn_1 to "1",
            R.id.btn_2 to "2",
            R.id.btn_3 to "3",
            R.id.btn_4 to "4",
            R.id.btn_5 to "5",
            R.id.btn_6 to "6",
            R.id.btn_7 to "7",
            R.id.btn_8 to "8",
            R.id.btn_9 to "9"
        ).forEach { (id, number) ->
            findViewById<Button>(id).setOnClickListener {
                calculator.onNumberClick(number)
                updateDisplay()
            }
        }

        findViewById<Button>(R.id.btn_dot).setOnClickListener {
            calculator.onDotClick()
            updateDisplay()
        }

        mapOf(
            R.id.btn_add to "+",
            R.id.btn_subtract to "-",
            R.id.btn_multiply to "×",
            R.id.btn_divide to "÷"
        ).forEach { (id, op) ->
            findViewById<Button>(id).setOnClickListener {
                calculator.onOperationClick(op)
            }
        }

        findViewById<Button>(R.id.btn_equals).setOnClickListener {
            calculator.onEqualsClick()
            updateDisplay()
        }

        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            calculator.onClear()
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        display.text = calculator.getDisplay()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        calculator.save(outState)
    }
}