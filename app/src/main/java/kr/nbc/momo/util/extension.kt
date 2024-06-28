package kr.nbc.momo.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.nbc.momo.R
import kr.nbc.momo.databinding.UiStateLoadingBinding
import kr.nbc.momo.databinding.UiStateNoResultBinding
import java.security.SecureRandom
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

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

fun View.setVisibleState(isVisible: Boolean){
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun String.getTimeGap(): String {
    var resultString: String
    try {
        val koreaZoneId = ZoneId.of("Asia/Seoul")
        val koreaTime = ZonedDateTime.now(koreaZoneId)
        val stringToTime = ZonedDateTime.parse(this)
        val duration = Duration.between(stringToTime, koreaTime)
        val period = Period.between(stringToTime.toLocalDate(), koreaTime.toLocalDate())

        resultString = when {
            period.years >= 1 -> "${period.years}년 전"
            period.months >= 1 -> "${period.months}월 전"
            period.days >= 1 -> "${period.days}일 전"
            duration.toHours() >= 1 -> "${duration.toHours()}시간 전"
            duration.toMinutes() >= 1 -> "${duration.toMinutes()}분 전"
            duration.seconds >= 1 -> "${duration.seconds}초 전"
            else -> "방금 전"
        }
    } catch (e: Exception) {
        resultString = "기록 없음"
    }

    return resultString
}

//fun String.toHashCode(): String {
//    val digest = try {
//        val str = this.plus(LocalDateTime.now())
//        val sh = MessageDigest.getInstance("SHA-256") // SHA-256 해시함수를 사용
//        sh.update(str.toByteArray()) // str의 문자열을 해싱하여 sh에 저장
//        val byteData = sh.digest() // sh 객체의 다이제스트를 얻는다.
//
//
//        //얻은 결과를 hex string으로 변환
//        val hexChars = "0123456789ABCDEF"
//        val hex = CharArray(byteData.size * 2)
//        for (i in byteData.indices) {
//            val v = byteData[i].toInt() and 0xff
//            hex[i * 2] = hexChars[v shr 4]
//            hex[i * 2 + 1] = hexChars[v and 0xf]
//        }
//
//        String(hex) //최종 결과를 String 으로 변환
//
//    } catch (e: NoSuchAlgorithmException) {
//        e.printStackTrace()
//        "" //오류 뜰경우 stirng은 blank값임
//    }
//    return digest
//}


fun randomStr() : String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val secureRandom = SecureRandom()
    return (1..15)
        .map { charset[secureRandom.nextInt(charset.size)] }
        .joinToString("")
}
fun ImageView.setGroupImageByUrlOrDefault(url: String?) {
    if (url.isNullOrEmpty() || url == "null") {
        this.load(R.drawable.icon_group_image)
    } else {
        this.load(url)
    }
}
fun ImageView.setThumbnailByUrlOrDefault(url: String?) {
    if (url.isNullOrEmpty() || url == "null") {
        this.load(R.drawable.icon_profile)
    } else {
        this.load(url)
    }
}

fun ImageView.setUploadImageByUrlOrDefault(url: String?) {
    if (url.isNullOrEmpty()) {
        this.load(R.drawable.image_default_upload)
    } else {
        this.load(url)
    }
}


fun UiStateLoadingBinding.setVisibleToGone() {
    prCircular.setVisibleToGone()
    tvLoading.setVisibleToGone()
    ivError.setVisibleToGone()
    tvError.setVisibleToGone()
}

fun UiStateLoadingBinding.setVisibleToInvisible() {
    prCircular.setVisibleToInvisible()
    tvLoading.setVisibleToInvisible()
    ivError.setVisibleToGone()
    tvError.setVisibleToGone()
}

fun UiStateLoadingBinding.setVisibleToVisible() {
    prCircular.setVisibleToVisible()
    tvLoading.setVisibleToVisible()
    ivError.setVisibleToGone()
    tvError.setVisibleToGone()
}

fun UiStateLoadingBinding.setVisibleToError() {
    prCircular.setVisibleToGone()
    tvLoading.setVisibleToGone()
    ivError.setVisibleToVisible()
    tvError.setVisibleToVisible()
}

fun UiStateNoResultBinding.setVisibleToVisible() {
    tvNoResult.setVisibleToVisible()
    ivNoResult.setVisibleToVisible()
}

fun UiStateNoResultBinding.setVisibleToGone() {
    tvNoResult.setVisibleToGone()
    ivNoResult.setVisibleToGone()
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.applicationWindowToken, 0)
}
fun Fragment.showNav() {
    val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
    nav.setVisibleToVisible()
}

fun Fragment.hideNav() {
    val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
    nav.setVisibleToGone()
}

fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis() + NUM_ONE
}

fun getAfterOneMonthTimeMillis(): Long {
    return System.currentTimeMillis() + ONE_MONTH_MILLIS
}

fun Context.isNetworkConnected(): Boolean { //네트워크 상태 확인
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}