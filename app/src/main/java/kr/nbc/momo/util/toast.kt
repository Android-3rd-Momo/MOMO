package kr.nbc.momo.util

import android.content.Context
import android.widget.Toast

fun makeToastWithStringRes(context:Context, stringRes: Int){
    Toast.makeText(context, context.getString(stringRes), Toast.LENGTH_SHORT).show()
}

fun makeToastWithString(context:Context, string: String){
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}