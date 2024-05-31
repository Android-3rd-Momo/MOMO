package kr.nbc.momo.presentation.chattingroom.util

import android.util.Log
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun setDateTimeFormatToMMDD(string: String): String{
    return try {
        Log.d("stringToDate", string)
        val parsedKoreaTime = ZonedDateTime.parse(string)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        formatter.format(parsedKoreaTime)
    }
    catch (e: Exception){
        Log.d("stringToDate", e.toString())
        "Error"
    }
}

fun setDateTimeFormatToYYYYmmDD(string: String): String{
    return try {
        Log.d("stringToDate", string)
        val parsedKoreaTime = ZonedDateTime.parse(string)
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        formatter.format(parsedKoreaTime)
    }
    catch (e: Exception){
        Log.d("stringToDate", e.toString())
        "Error"
    }
}
