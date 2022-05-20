package com.example.acalculator

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import java.text.SimpleDateFormat

@Parcelize
class OperationUi (
    val uuid: String,
    val expression : String,
    val result : String,
    val timestamp : Long
): Parcelable {
    
    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss")
        return sdf.format(Date(timestamp))
    }
}