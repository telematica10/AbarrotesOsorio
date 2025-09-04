package com.ajo.abarrotesOsorio.utils

import java.text.NumberFormat
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

        fun getFormattedPrice(args: Any?): String{
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            return formatter.format(args)
        }
    }
}