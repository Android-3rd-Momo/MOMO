package kr.nbc.momo.presentation.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentRootBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.mypage.group.MyGroupFragment
import kr.nbc.momo.presentation.mypage.profile.MyPageContainerFragment
import kr.nbc.momo.presentation.setup.SetUpFragment
import kr.nbc.momo.util.setVisibleToGone

@AndroidEntryPoint
class RootFragment : Fragment() {
    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()
        initViewPager()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentUser.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            //No action needed
                        }

                        is UiState.Success -> {
                            uiState.data?.let {
                                currentUser = it.userId
                            }
/*                            if (uiState.data != null) {
                                Log.d("currentUser", uiState.data.userId)
                                currentUser = uiState.data.userId
                            }*/
                            initView(currentUser)
                        }

                        is UiState.Error -> {
                            initView(currentUser)
                        }
                    }
                }
            }
        }
    }

    private fun initView(currentUser: String?) {
        if (currentUser == null) {
            binding.ivSetUp.setVisibleToGone()
        }
        binding.ivSetUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SetUpFragment())
                .addToBackStack(null)
                .commit()
        }


    }
    private fun initViewPager() {
        val viewPager = ViewPagerAdapter(this)
        viewPager.addFragment(MyPageContainerFragment())
        viewPager.addFragment(MyGroupFragment())
        binding.vpRoot.adapter = viewPager
        binding.vpRoot.isUserInputEnabled = false

        TabLayoutMediator(binding.tlRoot, binding.vpRoot) { tab, position ->
            when (position) {
                0 -> tab.setText(R.string.profile)
                1 -> tab.setText(R.string.myGroupList)
            }
        }.attach()
    }
}