package com.ajo.abarrotesOsorio.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utilities {
    companion object {

        fun getFormattedDate(date: Date): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(date)
        }
        fun getFormattedDateRegister(date: Date): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }
    }
}