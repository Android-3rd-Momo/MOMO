package kr.nbc.momo.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import kr.nbc.momo.R

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
                if (isKoreanConsonant(editType.last())) context.getString(R.string.a_max_edit_is_b_nun, editType, maxLength)
                else context.getString(R.string.a_max_edit_is_b_eun, editType, maxLength)

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
                if (isKoreanConsonant(editType.last())) context.getString(R.string.a_max_edit_is_b_nun, editType, maxLength)
                else context.getString(R.string.a_max_edit_is_b_eun, editType, maxLength)
            textNumberTextView.text = context.getString(R.string.a_divide_b, textLength, maxLength)
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