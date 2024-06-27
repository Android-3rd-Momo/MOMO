package kr.nbc.momo.presentation.group

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager

class CustomDatePickerDialog(
    context: Context,
    themeResId: Int,
    listener: OnDateSetListener,
    year: Int,
    month: Int,
    dayOfMonth: Int
) : DatePickerDialog(context, themeResId, listener, year, month, dayOfMonth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val width = dpToPx(context, 200)
        window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()

    }
}