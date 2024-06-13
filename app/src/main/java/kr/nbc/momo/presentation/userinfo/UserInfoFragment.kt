package kr.nbc.momo.presentation.userinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentUserInfoBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class UserInfoFragment : Fragment() {
    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserInfoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavHide()
        initUser()
        observeUserState()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomNavShow()
    }

    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        binding.prCircular.setVisibleToVisible()
                        binding.svUserInfo.setVisibleToGone()
                    }

                    is UiState.Success -> {
                        initView(uiState.data)
                        binding.prCircular.setVisibleToGone()
                        binding.svUserInfo.setVisibleToVisible()
                    }

                    is UiState.Error -> {

                    }
                }

            }
        }
    }

    private fun initUser() {
        lifecycleScope.launch {
            sharedViewModel.userId.observe(viewLifecycleOwner) {
                if (it != null) {
                    viewModel.userInfo(it)
                }
            }
        }
    }

    private fun initView(data: UserModel) {
        with(binding){
            if (data.userBackgroundThumbnailUrl.isNotEmpty()) {
                ivBackProfileThumbnail.load(data.userBackgroundThumbnailUrl)
            }
            ivUserProfileImage.setThumbnailByUrlOrDefault(data.userProfileThumbnailUrl)
            tvUserName.text = data.userName

            if (data.typeOfDevelopment.isEmpty()) {
                typeOfDevelopmentText.setVisibleToVisible()
            } else {
                initChip(chipGroupDevelopmentOccupations, data.typeOfDevelopment)
            }

            if (data.programOfDevelopment.isEmpty()) {
                programOfDevelopmentText.setVisibleToVisible()
            } else {
                initChip(chipProgramingLanguage, data.programOfDevelopment)
            }

            if (data.stackOfDevelopment.isNotEmpty()) {
                stackOfDevelopment.text = data.stackOfDevelopment
            }

            if (data.userPortfolioImageUrl.isEmpty()) ivPortfolioImage.setVisibleToGone()
            ivPortfolioImage.load(data.userPortfolioImageUrl)

            if (data.userPortfolioText.isNotEmpty()) {
                portfolio.text = data.userPortfolioText
            }

            if (data.userSelfIntroduction.isNotEmpty()) {
                tvUserSelfIntroduction.text = data.userSelfIntroduction
            }
        }

    }

    private fun initChip(chipGroup: ChipGroup, chipList: List<String>) {
        for (chipText in chipList) {
            val chip = Chip(requireContext()).apply {
                text = chipText
                setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.white
                    )
                )
                setChipBackgroundColorResource(
                    R.color.blue
                )
            }
            chipGroup.addView(chip)
        }
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

    private fun bottomNavHide() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.setVisibleToGone()
    }

    private fun bottomNavShow() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.setVisibleToVisible()
    }

}
