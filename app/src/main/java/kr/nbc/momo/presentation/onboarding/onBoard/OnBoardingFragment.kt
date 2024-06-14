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
import kr.nbc.momo.presentation.home.HomeFragment
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.presentation.onboarding.developmentType.DevelopmentProgramFragment
import kr.nbc.momo.presentation.onboarding.developmentType.DevelopmentTypeFragment
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
        addToList("모이개에 어서오세요", "개발자 커뮤니티에 오신것을\n" + "환영합니다", R.drawable.onboarding_illust1)
        addToList("다양한 개발자들이 한곳에 ", "프론트엔드, 백엔드 다양한 직군이\n" + "한곳에 모여있습니다", R.drawable.onboarding_illust3)
        addToList("단기 프로젝트도 OK", "프로젝트를 함께 개발할 동료들을\n" + "모이개에서 모집해보세요!", R.drawable.onboarding_illust2)
    }

}