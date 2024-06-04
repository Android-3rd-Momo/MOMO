package kr.nbc.momo.util

import android.view.View
import java.time.Duration
import java.time.Period
import java.time.ZoneId
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

fun View.setVisibleToGone() {
    this.visibility = View.GONE
}

fun View.setVisibleToVisible() {
    this.visibility = View.VISIBLE
}

fun View.setVisibleToInvisible() {
    this.visibility = View.INVISIBLE
}

fun String.getTimeGap(): String {
    var resultString: String
    try {
        val koreaZoneId = ZoneId.of("Asia/Seoul")
        val koreaTime = ZonedDateTime.now(koreaZoneId)
        val stringToTime = ZonedDateTime.parse(this)
        val duration = Duration.between(koreaTime, stringToTime)
        val period = Period.between(koreaTime.toLocalDate(), stringToTime.toLocalDate())

        resultString = when {
            period.years >= 1 -> "${period.years}년 전"
            period.months >= 1 -> "${period.months}월 전"
            period.days >= 1 -> "${period.days}일 전"
            duration.toHours() >= 1 -> "${duration.toHours()}시간 전"
            duration.toMinutes() >= 1 -> "${duration.toMinutes()}분 전"
            duration.seconds >= 1 -> "${duration.seconds}초 전"
            else -> "방금 전"
        }
    }catch (e: Exception){
        resultString = "기록 없음"
    }

    return resultString
}
