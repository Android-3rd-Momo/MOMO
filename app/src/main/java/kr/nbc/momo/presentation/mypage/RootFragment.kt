package kr.nbc.momo.presentation.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.databinding.FragmentRootBinding
import kr.nbc.momo.presentation.setup.SetUpFragment

@AndroidEntryPoint
class RootFragment : Fragment() {
    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initView()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() {
        binding.ivSetUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SetUpFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    private fun initViewPager() {
        val viewPager = ViewPagerAdapter(this)
        viewPager.addFragment(MyPageFragment())
        viewPager.addFragment(MyGroupFragment())
        binding.vpRoot.adapter = viewPager

        TabLayoutMediator(binding.tlRoot, binding.vpRoot) { tab, position ->
            when (position) {
                0 -> tab.text = "프로필"
                1 -> tab.text = "모임 내역"
            }
        }.attach()
    }
}