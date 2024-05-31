package kr.nbc.momo.presentation.chattingroom.util

import android.view.View
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun String.setDateTimeFormatToMMDD(): String {
    val parsedKoreaTime = ZonedDateTime.parse(this)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    return formatter.format(parsedKoreaTime)
}

fun String.setDateTimeFormatToYYYYmmDD(): String {
    val parsedKoreaTime = ZonedDateTime.parse(this)
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    return formatter.format(parsedKoreaTime)
}

fun View.setVisibleToGone(){
    this.visibility = View.GONE
}

fun View.setVisibleToVisible(){
    this.visibility = View.VISIBLE
}

fun View.setVisibleToInvisible(){
    this.visibility = View.INVISIBLE
}