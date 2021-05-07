package com.jk.prm.financemanager.utils

import java.text.SimpleDateFormat
import java.util.*

object Converter {

    fun convertToMonthAndYear(toConvert: String, pattern: String): Pair<Int, Int> {
        val date = SimpleDateFormat(pattern, Locale.US).parse(toConvert)
        val cal = Calendar.getInstance()
        cal.time = date!!
        val month = cal[Calendar.MONTH]
        val year = cal[Calendar.YEAR]
        return Pair(month, year)
    }

    fun convertToDay(toConvert: String, pattern: String): Int {
        val date = SimpleDateFormat(pattern, Locale.US).parse(toConvert)
        val cal = Calendar.getInstance()
        cal.time = date!!
        return cal[Calendar.DAY_OF_MONTH]
    }
}