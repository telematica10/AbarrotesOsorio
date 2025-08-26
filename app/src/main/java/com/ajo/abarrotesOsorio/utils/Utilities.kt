package com.ajo.abarrotesOsorio.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase de utilidades para funciones comunes en la aplicación.
 * El uso de un 'companion object' permite llamar a los métodos de forma estática,
 * sin necesidad de crear una instancia de la clase.
 */
class Utilities {
    companion object {
        /**
         * Formatea un objeto Date a un String con el formato "dd/MM/yyyy HH:mm:ss".
         * @param date El objeto Date que se desea formatear.
         * @return La fecha formateada como un String.
         */
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