package io.github.mobdev

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class CalculatorTest {
    private lateinit var calculator: Calculator

    @Before
    fun setUp() {
        calculator = Calculator()
    }

    @Test
    fun testAddition() {
        calculator.onNumberClick("5")
        calculator.onOperationClick("+")
        calculator.onNumberClick("3")
        val result = calculator.onEqualsClick()
        assertEquals("8", result)
    }

    @Test
    fun testSubtraction() {
        calculator.onNumberClick("10")
        calculator.onOperationClick("-")
        calculator.onNumberClick("3")
        val result = calculator.onEqualsClick()
        assertEquals("7", result)
    }

    @Test
    fun testMultiplication() {
        calculator.onNumberClick("4")
        calculator.onOperationClick("×")
        calculator.onNumberClick("5")
        val result = calculator.onEqualsClick()
        assertEquals("20", result)
    }

    @Test
    fun testDivision() {
        calculator.onNumberClick("20")
        calculator.onOperationClick("÷")
        calculator.onNumberClick("4")
        val result = calculator.onEqualsClick()
        assertEquals("5", result)
    }

    @Test
    fun testDivisionByZero() {
        calculator.onNumberClick("10")
        calculator.onOperationClick("÷")
        calculator.onNumberClick("0")
        val result = calculator.onEqualsClick()
        assertEquals("0", result)
    }

    @Test
    fun testClear() {
        calculator.onNumberClick("5")
        calculator.onNumberClick("3")
        calculator.onClear()
        assertEquals("0", calculator.getDisplay())
    }

    @Test
    fun testDotInput() {
        calculator.onNumberClick("5")
        calculator.onDotClick()
        calculator.onNumberClick("5")
        assertEquals("5.5", calculator.getDisplay())
    }

    @Test
    fun testChainedOperations() {
        calculator.onNumberClick("10")
        calculator.onOperationClick("+")
        calculator.onNumberClick("5")
        calculator.onOperationClick("×")
        calculator.onNumberClick("2")
        val result = calculator.onEqualsClick()
        assertEquals("30", result)
    }

    @Test
    fun testSecondaryDisplay() {
        calculator.onNumberClick("5")
        calculator.onOperationClick("+")
        val secondary = calculator.getSecondaryDisplay()
        assertEquals("5 +", secondary)
    }

    @Test
    fun testMultipleDecimals() {
        calculator.onNumberClick("5")
        calculator.onDotClick()
        calculator.onDotClick()
        calculator.onNumberClick("5")
        assertEquals("5.5", calculator.getDisplay())
    }
}