package kr.nbc.momo.presentation.onboarding.onBoard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.databinding.FragmentOnBoardBinding
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.onboarding.GetStartedViewPagerAdapter
import kr.nbc.momo.presentation.onboarding.login.LoginFragment
import kr.nbc.momo.presentation.onboarding.term.TermFragment
import me.relex.circleindicator.CircleIndicator3

/**
 * A simple [Fragment] subclass.
 * Use the [onBoardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class onBoardingFragment : Fragment() {
    private var _binding: FragmentOnBoardBinding? = null
    private val binding get() = _binding!!

    private var titleList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOnBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postToList()

        val viewPager2 = binding.viewPager2
        viewPager2.adapter = OnBoardingViewPagerAdapter(titleList, descList, imageList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = binding.indicator
        indicator.setViewPager(viewPager2)

        binding.buttonLogin.setOnClickListener {
            //아이디 비번 조건넣기
            val fragmentLogin = LoginFragment()
            fragmentLogin.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            fragmentLogin.show(parentFragmentManager, fragmentLogin.tag)
        }

        binding.buttonWithoutLogin.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun addToList(title: String, description: String, image: Int) {
        titleList.add(title)
        descList.add(description)
        imageList.add(image)
    }

    private fun postToList() {
        addToList("안녕하세요", "첫번째입니다", R.drawable.onboarding_illust1)
        addToList("안녕하세요", "두번째입니다", R.drawable.onboardingillust2)
        addToList("안녕하세요", "세번째입니다", R.drawable.onboardingillust3)
    }
   
}