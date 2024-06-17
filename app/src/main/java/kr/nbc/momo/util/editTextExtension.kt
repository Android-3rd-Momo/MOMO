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
    val unicode = char.code
    // 한글 음절에서 받침이 있는지 여부를 판단
    // 받침이 있는 경우: (unicode - 0xAC00) % 28 != 0
    // 받침이 없는 경우: (unicode - 0xAC00) % 28 == 0
    return (unicode - 0xAC00) % 28 == 0
}