package com.example.acalculator

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Operation (var expression : String, var result : String) {
    val timestamp : Long = Date().time
    override fun toString(): String = "$expression = $result"
}