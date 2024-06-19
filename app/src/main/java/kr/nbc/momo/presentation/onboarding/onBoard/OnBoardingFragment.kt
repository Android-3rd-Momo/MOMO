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

    private fun addToList(titleId: Int, descriptionId: Int, image: Int) {
        titleList.add(getString(titleId))
        descList.add(getString(descriptionId))
        imageList.add(image)
    }

    private fun postToList() {
        addToList(R.string.welcome_to_moigae, R.string.welcome_to_dev_community, R.drawable.onboarding_illust1)
        addToList(R.string.many_dev, R.string.any_come, R.drawable.onboarding_illust3)
        addToList(R.string.short_ok, R.string.recruit_member, R.drawable.onboarding_illust2)
    }

}