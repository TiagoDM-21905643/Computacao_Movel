package com.example.acalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {

    private val model = Calculator()

    fun getDisplayValue(): String {
        return model.display
    }

    fun onClickSymbol(symbol: String, checkSymbol: Boolean): String {
        return model.insertSymbol(symbol, checkSymbol)
    }

    fun onClickNumber(num: Int): String {
        return model.insertNumber(num)
    }

    fun onClickEquals(percentage: Boolean): String {
        return model.performOperation(percentage)
    }

    fun onClickClear(): String {
        return model.clear()
    }

    fun onClickDelete(): String {
        return model.delete()
    }

    fun getHistory(callback: (List<Operation>) -> Unit) {
        model.getHistory { operations ->
            CoroutineScope(Dispatchers.Main).launch {
                callback(operations)
            }
        }
    }
}