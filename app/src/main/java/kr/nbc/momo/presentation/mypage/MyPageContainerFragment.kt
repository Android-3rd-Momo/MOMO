package kr.nbc.momo.presentation.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R

@AndroidEntryPoint
class MyPageContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_page_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyPageFragment())
                .commit()
        }
    }

    fun switchToEditPage() {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EditMyPageFragment())
            .addToBackStack(null)
            .commit()
    }

    fun switchToMyPage() {
        childFragmentManager.popBackStack()
    }
}