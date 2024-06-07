package kr.nbc.momo.presentation.onboarding.onBoard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentOnBoardingBinding
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.onboarding.login.SignInFragment

class OnBoardingFragment : Fragment() {
    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private var titleList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener{
            val fragmentSignIn = SignInFragment()
            fragmentSignIn.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            fragmentSignIn.show(parentFragmentManager, fragmentSignIn.tag)
        }

        binding.buttonWithoutLogin.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        postToList()

        val viewPager2 = binding.viewPager2
        viewPager2.adapter = OnBoardingViewPagerAdapter(titleList,descList,imageList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = binding.indicator
        indicator.setViewPager(viewPager2)
    }

    private fun addToList(title: String, description: String, image: Int) {
        titleList.add(title)
        descList.add(description)
        imageList.add(image)
    }

    private fun postToList() {
        addToList("모이개에 오신것을 환영합니다", "모두 모여라 이런 개발자 모임에 오셨군요", R.drawable.onboarding_illust1)
        addToList("프로젝트에 같이 참여하실 인원을 구하기 어려우신가요? ", "저희 앱에서 구해보세요", R.drawable.onboardingillust2)
        addToList("쉽고 간편하게", "모두 모여라 이런 개발자 모임에서 팀원을 구해보세요", R.drawable.onboardingillust3)
    }

}