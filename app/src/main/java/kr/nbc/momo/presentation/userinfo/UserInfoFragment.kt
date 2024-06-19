package kr.nbc.momo.presentation.userinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentUserInfoBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.util.makeToastWithStringRes
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class UserInfoFragment : Fragment(), PopupMenu.OnMenuItemClickListener  {
    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserInfoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var userId: String

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
        observeReportUser()
        observeBlockUser()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomNavShow()
    }

    private fun observeReportUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reportUserState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 로딩 처리 (필요한 경우)
                    }

                    is UiState.Success -> {
                        parentFragmentManager.popBackStack()
                        makeToastWithStringRes(requireContext(), R.string.user_report_success)
//                        Toast.makeText(requireContext(), getString(R.string.user_report_success), Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Error -> {
                        Log.d("error", uiState.message)
                    }
                }

            }
        }
    }

    private fun observeBlockUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.blockUserState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 로딩 처리 (필요한 경우)
                    }

                    is UiState.Success -> {
                        parentFragmentManager.popBackStack()
                        makeToastWithStringRes(requireContext(), R.string.user_block_success)
//                        Toast.makeText(requireContext(), getString(R.string.user_block_success), Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Error -> {
                        Log.d("error", uiState.message)
                    }
                }

            }
        }
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
                        userId = uiState.data.userId
                    }

                    is UiState.Error -> {
                        parentFragmentManager.popBackStack()
                        makeToastWithStringRes(requireContext(), R.string.failed_get_user_info)
//                        Toast.makeText(requireContext(), getString(R.string.failed_get_user_info), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    private fun initUser() {
        lifecycleScope.launch {
            sharedViewModel.userId.collectLatest {
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

            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            btnPopUp.setOnClickListener {
                showPopup(btnPopUp)
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

    private fun showPopup(v: View) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.popoup_menu_user, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu1 -> {
                viewModel.reportUser(userId)
                viewModel.blockUser(userId)
            }

            R.id.menu2 -> {
                viewModel.blockUser(userId)
            }
        }

        return item != null
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
