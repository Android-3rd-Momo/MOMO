package kr.nbc.momo.presentation.mypage.profile

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentEditMyPageBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.util.MY_PAGE_TEXT_MAX_LENGTH
import kr.nbc.momo.util.SELF_INTRODUCE_MAX_LENGTH
import kr.nbc.momo.util.addTextWatcherWithError
import kr.nbc.momo.util.hideKeyboard
import kr.nbc.momo.util.hideNav
import kr.nbc.momo.util.isValidName
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setUploadImageByUrlOrDefault
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible
import kr.nbc.momo.util.showNav


@AndroidEntryPoint
class EditMyPageFragment : Fragment() {
    private var _binding: FragmentEditMyPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: UserModel? = null
    private var profileImageUri: Uri? = null
    private var backgroundImageUri: Uri? = null
    private var portfolioImageUri: Uri? = null
    private var isProfileImageChange = false
    private var isBackgroundImageChange = false
    private var isPortfolioImageChange = false

    private val pickProfileImage = setUpImagePicker(ImageType.PROFILE)
    private val pickBackgroundImage = setUpImagePicker(ImageType.BACKGROUND)
    private val pickPortfolioImage = setUpImagePicker(ImageType.PORTFOLIO)


    private fun setUpImagePicker(imageType: ImageType) = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if(uri != null){
            when (imageType){
                ImageType.PROFILE -> {
                    profileImageUri = uri
                    isProfileImageChange = true
                    binding.ivUserProfileImage.load(uri)
                }
                ImageType.BACKGROUND -> {
                    backgroundImageUri = uri
                    isBackgroundImageChange = true
                    binding.ivBackProfileThumbnail.load(uri)
                }
                ImageType.PORTFOLIO -> {
                    portfolioImageUri = uri
                    isPortfolioImageChange = true
                    binding.ivPortfolioImage.load(uri)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNav()
        observeUserProfile()
        initEventHandlers()
    }

    override fun onResume() {
        super.onResume()
        hideNav()
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
                            state.data?.let { data ->
                                currentUser = data
                                initView(data)
                            }
/*                            if (state.data != null) {
                                currentUser = state.data
                                initView(state.data)
                            }*/
                            binding.scrollView.setVisibleToVisible()
                        }

                        is UiState.Error -> {
                            makeToastWithString(requireContext(), state.message)
                            binding.includeUiState.setVisibleToError()
                            binding.scrollView.setVisibleToGone()
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
                            binding.includeUiState.setVisibleToVisible()
                            binding.scrollView.setVisibleToGone()
                        }

                        is UiState.Success -> {
                            (parentFragment as? MyPageContainerFragment)?.switchToMyPage()
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
            etUserName.setText(user.userName)
            etUserSelfIntroduction.setText(user.userSelfIntroduction)
            etStackOfDevelopment.setText(user.stackOfDevelopment)
            etPortfolio.setText(user.userPortfolioText)
            ivUserProfileImage.setThumbnailByUrlOrDefault(user.userProfileThumbnailUrl)
            ivBackProfileThumbnail.load(user.userBackgroundThumbnailUrl)
            ivPortfolioImage.setUploadImageByUrlOrDefault(user.userPortfolioImageUrl)

            setChipGroup(resources.getStringArray(R.array.chipGroupDevelopmentOccupations), cgTypeTag, user.typeOfDevelopment)
            setChipGroup(resources.getStringArray(R.array.chipProgramingLanguage), cgProgramTag, user.programOfDevelopment)
        }

        binding.etStackOfDevelopment.addTextWatcherWithError(MY_PAGE_TEXT_MAX_LENGTH, getString(R.string.stack), binding.btnCompleteEdit, binding.tvCountStackEditText)
        binding.etPortfolio.addTextWatcherWithError(MY_PAGE_TEXT_MAX_LENGTH, getString(R.string.portfolio), binding.btnCompleteEdit, binding.tvCountPortfolioEditText)
        binding.etUserSelfIntroduction.addTextWatcherWithError(SELF_INTRODUCE_MAX_LENGTH, getString(R.string.self_introduce), binding.btnCompleteEdit)
    }

    private fun initEventHandlers() {
        binding.ivEditBackProfileThumbnail.setOnClickListener {
            showImageOptionDialog(ImageType.BACKGROUND)
        }
        binding.ivEditProfileImage.setOnClickListener {
            showImageOptionDialog(ImageType.PROFILE)
        }
        binding.ivPortfolioImage.setOnClickListener {
            showImageOptionDialog(ImageType.PORTFOLIO)
        }
        binding.btnCompleteEdit.setOnClickListener {
            if (validName()) {
                saveProfileInfo()
                requireActivity().hideKeyboard()
                observeUserProfileUpdate()
            }
        }
    }

    private fun showImageOptionDialog(imageType: ImageType) {
        val options = ImageOption.entries.map { it.getOptionText(requireContext()) }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(options) { _, position ->
            when (ImageOption.entries[position]) {
                ImageOption.PICK_IMAGE -> pickImage(imageType)
                ImageOption.DELETE_IMAGE -> deleteImage(imageType)
            }
        }
        builder.show()
    }

    private fun pickImage(imageType: ImageType) {
        when (imageType) {
            ImageType.PROFILE -> pickVisualMediaRequest(pickProfileImage)
            ImageType.BACKGROUND -> pickVisualMediaRequest(pickBackgroundImage)
            ImageType.PORTFOLIO -> pickVisualMediaRequest(pickPortfolioImage)
        }
    }

    private fun pickVisualMediaRequest(launcher: ActivityResultLauncher<PickVisualMediaRequest>) {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun deleteImage(imageType: ImageType) {
        when (imageType) {
            ImageType.PROFILE -> {
                profileImageUri = null
                isProfileImageChange = true
                binding.ivUserProfileImage.setThumbnailByUrlOrDefault(null)
            }

            ImageType.BACKGROUND -> {
                backgroundImageUri = null
                isBackgroundImageChange = true
                binding.ivBackProfileThumbnail.setImageResource(R.color.blue)
            }

            ImageType.PORTFOLIO -> {
                portfolioImageUri = null
                isPortfolioImageChange = true
                binding.ivPortfolioImage.setUploadImageByUrlOrDefault(null)
            }
        }
    }

    private fun saveProfileInfo() {
        currentUser?.let { currentUser ->
            val updatedUserModel = currentUser.copy(
                userName = binding.etUserName.text.toString(),
                userSelfIntroduction = binding.etUserSelfIntroduction.text.toString(),
                stackOfDevelopment = binding.etStackOfDevelopment.text.toString(),
                userPortfolioText = binding.etPortfolio.text.toString(),
                typeOfDevelopment = getChipText(binding.cgTypeTag),
                programOfDevelopment = getChipText(binding.cgProgramTag),
                userProfileThumbnailUrl = if (isProfileImageChange) profileImageUri?.toString() ?: "" else currentUser.userProfileThumbnailUrl,
                userBackgroundThumbnailUrl = if (isBackgroundImageChange) backgroundImageUri?.toString() ?: "" else currentUser.userBackgroundThumbnailUrl,
                userPortfolioImageUrl = if (isPortfolioImageChange) portfolioImageUri?.toString() ?: "" else currentUser.userPortfolioImageUrl
            )
            viewModel.saveUserProfile(updatedUserModel)
            sharedViewModel.updateUser(updatedUserModel)
            isProfileImageChange = false
            isBackgroundImageChange = false
            isPortfolioImageChange = false
        }
    }

    private fun getChipText(chipGroup: ChipGroup): List<String> {
        val textList = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                textList.add(chip.text.toString())
            }
        }
        return textList
    }

    private fun validName(): Boolean {
        val name = binding.etUserName.text.toString()
        var isValid = true

        if (name.isEmpty()) {
            binding.etUserName.error = getString(R.string.please_edit_name)
            isValid = false
        } else if (!name.isValidName()) {
            binding.etUserName.error = getString(R.string.edit_name_error)
            isValid = false
        } else {
            binding.etUserName.error = null
        }

        return isValid
    }

    private fun setChipGroup(chipList: Array<String>, chipGroup: ChipGroup, selectedChips: List<String>) {
        chipGroup.removeAllViews()
        chipList.forEach { chipText ->
            val chip = Chip(requireContext()).apply {
                text = chipText
                isCheckable = true
                isChecked = selectedChips.contains(chipText)
                setOnCheckedChangeListener { _, isChecked ->
                    isChipSelected(this, isChecked)
                }
                isChipSelected(this, isChecked)
            }
            chipGroup.addView(chip)
        }
    }

    private fun isChipSelected(chip: Chip, isChecked: Boolean) {
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

    override fun onPause() {
        super.onPause()
        showNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}