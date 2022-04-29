package com.example.acalculator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.roundToInt

object Calculator {

    var display: String = "0"
    private val history = mutableListOf<Operation>()

    private var lastActionEquals = false
    private var lastActionClear = false
    private var errorActive = false
    private var symbols = listOf('+', '-', '*', '/')

    private fun setBoolValidators(lastActionEquals: Boolean = false, lastActionClear: Boolean = false, errorActive: Boolean = false) {
        this.lastActionEquals = lastActionEquals
        this.lastActionClear = lastActionClear
        this.errorActive = errorActive
    }

    fun insertNumber(num: Int): String {
        if (display.equals("0") || lastActionEquals || lastActionClear) {
            display = "$num"
        } else {
            display += "$num"
        }
        setBoolValidators()
        return display
    }
    fun insertSymbol(symbol: String, checkSymbol: Boolean): String {
        if (checkSymbol && symbols.contains(display[display.length - 1])) {
            display = display.substring(0, display.length - 1)
        }
        when {
            errorActive -> display = "0$symbol"
            lastActionEquals -> display = symbol
            else -> display += symbol
        }
        setBoolValidators()
        return display
    }

    fun performOperation(percentage: Boolean, onSaved: () -> Unit): String {
        if (errorActive) {
            display = "0"
        } else {
            val expressionStr = display
            val expression = ExpressionBuilder(expressionStr).build()
            try {
                if (percentage) {
                    if ((expression.evaluate() / 100) % 1 == 0.0) {
                        display = (expression.evaluate() / 100).roundToInt().toString()
                    } else {
                        display = (expression.evaluate() / 100).toString()
                    }
                } else {
                    if (expression.evaluate() % 1 == 0.0) {
                        display = expression.evaluate().roundToInt().toString()
                    } else {
                        display = expression.evaluate().toString()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        addToHistory(expressionStr, display)
                        onSaved()
                    }
                }
            } catch (e: Exception) {
                display = "ERROR"
                errorActive = true
            }
        }
        lastActionEquals = true
        lastActionClear = false

        return display
    }

    fun clear(): String {
        display = "0"
        setBoolValidators(lastActionClear = true)
        return display
    }
    fun delete(): String {
        if (display.length > 1) {
            display = display.substring(0, display.length - 1)
        } else if (display.isNotEmpty() && !display.equals("0")) {
            display = "0"
        }
        setBoolValidators()
        return display
    }

    suspend fun addToHistory(expression: String, result: String) {
        history.add(Operation(expression, result))
    }

    fun getHistory(callback: (List<Operation>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            callback(history.toList())
        }
    }
}