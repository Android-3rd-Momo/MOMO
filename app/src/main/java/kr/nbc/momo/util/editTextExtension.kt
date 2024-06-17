package kr.nbc.momo.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView

fun TextView.addTextWatcherWithError(maxLength: Int, editType: String, btn: Button) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            //No action needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val textLength = this@addTextWatcherWithError.text.length
            val errorText =
                if (isKoreanConsonant(editType.last())) "${editType}는 ${maxLength}자까지 작성 가능합니다."
                else "${editType}은 ${maxLength}자까지 작성 가능합니다."

            if (textLength > maxLength) {
                this@addTextWatcherWithError.error = errorText
                btn.isEnabled = false
            } else {
                this@addTextWatcherWithError.error = null
                btn.isEnabled = true
            }
        }

        override fun afterTextChanged(s: Editable?) {
            //No action needed
        }

    })
}

fun TextView.addTextWatcherWithError(maxLength: Int, editType: String, btn: Button, textNumberTextView: TextView) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            //No action needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val textLength = this@addTextWatcherWithError.text.length
            val errorText =
                if (isKoreanConsonant(editType.last())) "${editType}는 ${maxLength}자까지 작성 가능합니다."
                else "${editType}은 ${maxLength}자까지 작성 가능합니다."
            textNumberTextView.text = "${textLength}/${maxLength}"
            if (textLength > maxLength) {
                this@addTextWatcherWithError.error = errorText
                btn.isEnabled = false
            } else {
                this@addTextWatcherWithError.error = null
                btn.isEnabled = true
            }
        }

        override fun afterTextChanged(s: Editable?) {
            //No action needed
        }

    })
}

fun isKoreanConsonant(char: Char): Boolean {
    val unicodeBlock = Character.UnicodeBlock.of(char)
    return unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO || // 초성 자모
            unicodeBlock == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO || // 중성 자모
            unicodeBlock == Character.UnicodeBlock.HANGUL_SYLLABLES // 한글 음절
}