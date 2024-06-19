package kr.nbc.momo.presentation.mypage

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentMyPageBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.read.EditReadGroupFragment
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.util.addTextWatcherWithError
import kr.nbc.momo.util.hideKeyboard
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setUploadImageByUrlOrDefault
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
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
                                isLogin()
                                currentUser = state.data
                                initView(state.data)
                            } else {
                                isLogOut()
                            }
                            binding.scrollView.setVisibleToVisible()
                        }

                        is UiState.Error -> {
                            binding.includeUiState.setVisibleToError()
                            binding.scrollView.setVisibleToGone()
                            clearUserInfo()
                            Log.d("mypage error", state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeUserProfileUpdate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfileUpdate.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            Log.d("test", "loading")
                            binding.includeUiState.setVisibleToVisible()
                            binding.scrollView.setVisibleToGone()
                        }

                        is UiState.Success -> {
                            Log.d("test", "Success")
                            binding.includeUiState.setVisibleToGone()
                            sharedViewModel.getCurrentUser()
                            binding.scrollView.setVisibleToVisible()
                        }

                        is UiState.Error -> {
                            Log.d("test", "Error")
                            binding.includeUiState.setVisibleToError()
                            binding.scrollView.setVisibleToGone()
                            Log.d("mypage error", state.message)
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

    private fun clearUserInfo() {
        with(binding) {
            tvUserName.text = ""
            tvUserSelfIntroduction.text = ""
            tvStackOfDevelopment.text = ""
            tvPortfolio.text = ""
            cgTypeTag.removeAllViews()
            cgProgramTag.removeAllViews()
            ivUserProfileImage.setThumbnailByUrlOrDefault(null)
            ivBackProfileThumbnail.setImageDrawable(null)
            ivPortfolioImage.setThumbnailByUrlOrDefault(null)
        }
    }

    private fun initEventHandler() {
        binding.ivEditProfile.setOnClickListener {
/*            val editMyPageFragment = EditMyPageFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editMyPageFragment)
                .addToBackStack("MyPageFragment")
                .commit()*/
        }
    }

    private fun isLogin() {
        binding.clUserDetailInfo.setVisibleToVisible()
        binding.ivEditProfile.setVisibleToVisible()
        binding.btnGoOnBoarding.setVisibleToGone()
    }

    private fun isLogOut() {
        binding.clUserDetailInfo.setVisibleToGone()
        binding.ivEditProfile.setVisibleToGone()
        binding.tvUserName.setText(R.string.need_login)
        binding.btnGoOnBoarding.setVisibleToVisible()
    }

    private fun updateChipAppearance(chip: Chip, isChecked: Boolean) {
        chip.setTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                if (isChecked) R.color.white else R.color.tv_chip_state_color
            )
        )
        chip.setChipBackgroundColorResource(
            if (isChecked) R.color.blue else R.color.bg_chip_state_color
        )
    }

    private fun setSelectedChips(chipGroup: ChipGroup, selectedChips: List<String>) {
        chipGroup.removeAllViews()
        selectedChips.forEach { chipText ->
            chipGroup.addView(Chip(requireContext()).apply {
                text = chipText
                isCheckable = false
                isChecked = true
                updateChipAppearance(this, true)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}