package kr.nbc.momo.presentation.chattingroom.util

import android.util.Log
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun setDateTimeFormatToKorea(string: String): String{
    return try {
        Log.d("stringToDate", string)
        val parsedKoreaTime = ZonedDateTime.parse(string)
        val formatter = DateTimeFormatter.ofPattern("HH:MM")
        formatter.format(parsedKoreaTime)
    }
    catch (e: Exception){
        Log.d("stringToDate", e.toString())
        "Error"
    }
}
