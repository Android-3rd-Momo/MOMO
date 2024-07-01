package kr.nbc.momo.presentation.mypage.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentMyPageBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.OnBoardingActivity
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setUploadImageByUrlOrDefault
import kr.nbc.momo.util.setVisibleState
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: UserModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()
        initEventHandler()
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentUser.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.includeUiState.setVisibleToVisible()
                            binding.scrollView.setVisibleToGone()
                        }

                        is UiState.Success -> {
                            binding.includeUiState.setVisibleToGone()
                            if (state.data != null) {
                                isLogin(true)
                                currentUser = state.data
                                initView(state.data)
                            } else {
                                isLogin(false)
                            }
                            binding.scrollView.setVisibleToVisible()
                        }

                        is UiState.Error -> {
                            binding.includeUiState.setVisibleToError()
                            binding.scrollView.setVisibleToGone()
                            Log.d("mypage error", state.message)
                            makeToastWithString(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    private fun initView(user: UserModel) {
        with(binding) {
            tvUserName.text = user.userName
            tvUserSelfIntroduction.text = user.userSelfIntroduction.ifEmpty { getString(R.string.introduce_yourself) }
            tvStackOfDevelopment.text = user.stackOfDevelopment.ifEmpty { getString(R.string.introduce_stack) }
            tvPortfolio.text = user.userPortfolioText.ifEmpty { getString(R.string.introduce_portfolio) }
            ivUserProfileImage.setThumbnailByUrlOrDefault(user.userProfileThumbnailUrl)
            ivBackProfileThumbnail.load(user.userBackgroundThumbnailUrl)
            ivPortfolioImage.setUploadImageByUrlOrDefault(user.userPortfolioImageUrl)

            if (user.typeOfDevelopment.isEmpty()) {
                tvEmptyTypeTag.setVisibleToVisible()
                cgTypeTag.setVisibleToGone()
            } else {
                tvEmptyTypeTag.setVisibleToGone()
                setSelectedChips(cgTypeTag, user.typeOfDevelopment)
            }

            if (user.programOfDevelopment.isEmpty()) {
                tvEmptyProgramTag.setVisibleToVisible()
                cgProgramTag.setVisibleToGone()
            } else {
                tvEmptyProgramTag.setVisibleToGone()
                setSelectedChips(cgProgramTag, user.programOfDevelopment)
            }
        }
    }

    private fun initEventHandler() {
        binding.ivEditProfile.setOnClickListener {
            (parentFragment as? MyPageContainerFragment)?.switchToEditPage()
        }
        binding.btnGoOnBoarding.setOnClickListener {
            val intent = Intent(requireActivity(), OnBoardingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isLogin(isLogIn: Boolean){
        binding.apply {
            clUserDetailInfo.setVisibleState(isLogIn)
            ivEditProfile.setVisibleState(isLogIn)
            btnGoOnBoarding.setVisibleState(!isLogIn)
            if(!isLogIn){
                tvUserName.setText(R.string.need_login)
            }
        }
    }

    private fun setSelectedChips(chipGroup: ChipGroup, selectedChips: List<String>) {
        chipGroup.removeAllViews()
        selectedChips.forEach { chipText ->
            chipGroup.addView(Chip(requireContext()).apply {
                text = chipText
                isCheckable = false
                setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.base_chip_text
                    )
                )
                setChipBackgroundColorResource(R.color.base_chip_bg)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}