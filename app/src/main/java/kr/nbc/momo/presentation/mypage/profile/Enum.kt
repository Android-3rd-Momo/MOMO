package kr.nbc.momo.presentation.mypage.profile

import android.content.Context
import androidx.annotation.StringRes
import kr.nbc.momo.R

enum class ImageType {
    PROFILE, BACKGROUND, PORTFOLIO
}
enum class ImageOption(@StringRes val text: Int) {
    PICK_IMAGE(R.string.pick_image),
    DELETE_IMAGE(R.string.delete_image);

    fun getOptionText(context: Context): String{
        return context.getString(text)
    }

}