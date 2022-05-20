package com.example.acalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val model = Calculator(
        CalculatorDatabase.getInstance(application).operationDao()
    )

    fun getDisplayValue(): String {
        return model.display
    }

    fun onClickSymbol(symbol: String, checkSymbol: Boolean): String {
        return model.insertSymbol(symbol, checkSymbol)
    }

    fun onClickNumber(num: Int): String {
        return model.insertNumber(num)
    }

    fun onClickEquals(percentage: Boolean, onSaved: () -> Unit): String {
        return model.performOperation(percentage, onSaved)
    }

    fun onClickClear(): String {
        return model.clear()
    }

    fun onClickDelete(): String {
        return model.delete()
    }

    fun onGetHistory(callback: (List<OperationUi>) -> Unit) {
        model.getHistory(callback)
    }
}