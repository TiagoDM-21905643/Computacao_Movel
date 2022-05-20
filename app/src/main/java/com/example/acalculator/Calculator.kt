package com.example.acalculator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.*
import kotlin.math.roundToInt

abstract class Calculator(private val dao: OperationDao) {

    var display: String = "0"
    // private val history = mutableListOf<OperationRoom>()

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

    open fun performOperation(percentage: Boolean, onSaved: () -> Unit): String {
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

    /*fun getHistory(callback: (List<OperationUi>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val operations = dao.getAll()
            callback(operations.map {
                OperationUi(it.uuid, it.expression, it.result, it.timestamp)
            })
        }
    }*/

    abstract fun insertOperations(operations: List<OperationUi>, onFinished: (List<OperationUi>) -> Unit)
    abstract fun getLastOperation(callback: (String) -> Unit)
    abstract fun deleteOperation(uuid: String, onFinished: () -> Unit)
    abstract fun deleteAllOperations(onFinished: () -> Unit)
    abstract fun getHistory(onFinished: (List<OperationUi>) -> Unit)


}