package com.example.acalculator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CalculatorRoom(private val dao: OperationDao): Calculator(dao) {

    override fun performOperation(percentage: Boolean, onSaved: () -> Unit): String {
        val currentExpression = display
        return super.performOperation(percentage) {
            val operation = OperationRoom(expression = currentExpression, result = display, timestamp = Date().time)
            CoroutineScope(Dispatchers.IO).launch { dao.insert(operation) }
            onSaved()
        }
    }

    override fun insertOperations(
        operations: List<OperationUi>,
        onFinished: (List<OperationUi>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val history = operations.map {
                OperationRoom(it.uuid, it.expression, it.result, it.timestamp)
            }
            dao.insertAll(history)
            onFinished(operations)
        }
    }

    override fun getLastOperation(callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            getHistory { callback(it.last().expression) }
        }
    }

    override fun deleteOperation(uuid: String, onFinished: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = dao.delete(uuid)
            if (result == 1) onFinished()
        }
    }

    override fun deleteAllOperations(onFinished: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAll()
            onFinished()
        }
    }

    override fun getHistory(onFinished: (List<OperationUi>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val operations = dao.getAll()
            onFinished(
                operations.map {
                    OperationUi(it.uuid, it.expression, it.result, it.timestamp)
                }
            )
        }
    }

}