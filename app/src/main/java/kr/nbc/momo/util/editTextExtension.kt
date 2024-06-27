package kr.nbc.momo.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
                if (isKoreanConsonant(editType.last())) context.getString(
                    R.string.a_max_edit_is_b_nun,
                    editType,
                    maxLength
                )
                else context.getString(R.string.a_max_edit_is_b_eun, editType, maxLength)

            if (textLength > maxLength) {
                this@addTextWatcherWithError.error = errorText
                btn.isEnabled = false
            } else if (s?.contains("\n\n\n") == true) {
                this@addTextWatcherWithError.error = context.getString(R.string.line_change_error)
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

fun TextView.addTextWatcherWithError(
    maxLength: Int,
    editType: String,
    btn: Button,
    textNumberTextView: TextView
) {
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
                if (isKoreanConsonant(editType.last())) context.getString(
                    R.string.a_max_edit_is_b_nun,
                    editType,
                    maxLength
                )
                else context.getString(R.string.a_max_edit_is_b_eun, editType, maxLength)
            textNumberTextView.text = context.getString(R.string.a_divide_b, textLength, maxLength)
            if (textLength > maxLength) {
                this@addTextWatcherWithError.error = errorText
                btn.isEnabled = false
            } else if (s?.contains("\n\n\n") == true) {
                this@addTextWatcherWithError.error = context.getString(R.string.line_change_error)
                btn.isEnabled = false
            }
            else {
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

fun EditText.addValidationTextWatcher(
    validator: (String) -> Boolean,
    errorText: String,
    validationMap: MutableMap<EditText, Boolean>,
    checkButton: Button? = null
) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString()
            val isValid = validator(text)
            validationMap[this@addValidationTextWatcher] = isValid
            error = if (isValid) null else errorText

            checkButton?.let {
                it.isEnabled = isValid
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

fun Fragment.checkDuplicate(
    validator: (String) -> Boolean,
    errorText: String,
    validationMap: MutableMap<EditText, Boolean>,
    checkFunction: suspend (String) -> Boolean,
    editText: EditText,
    onValid: () -> Unit,
    successMessage: Int,
) {
    if (!requireContext().isNetworkConnected()) {
        makeToastWithStringRes(requireContext(), R.string.network_error)
        return
    }

    val text = editText.text.toString()
    if (!validator(text)) {
        editText.error = errorText
    } else {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val isDuplicate = checkFunction(text)
                if (isDuplicate) {
                    editText.error = getString(R.string.duplication_error)
                    validationMap[editText] = false
                } else {
                    editText.error = null
                    makeToastWithStringRes(requireContext(), successMessage)
                    onValid()
                    validationMap[editText] = true
                }
            } catch (e: Exception) {
                Log.e("SignUp", "Failed to Check UserId Validity", e)
            }
        }
    }
}