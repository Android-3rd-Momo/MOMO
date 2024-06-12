package kr.nbc.momo.presentation.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentMyPageBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.presentation.setup.SetUpFragment
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

    private var isEditMode = false
    private var currentUser: UserModel? = null
    private var profileImageUri: Uri? = null
    private var backgroundImageUri: Uri? = null
    private var portfolioImageUri: Uri? = null

    private val pickProfileImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            profileImageUri = uri
            binding.ivUserProfileImage.load(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val pickBackgroundImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            backgroundImageUri = uri
            binding.ivBackProfileThumbnail.load(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val pickPortfolioImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            portfolioImageUri = uri
            binding.ivPortfolioImage.load(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()
        eachEventHandler()
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentUser.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
//                            binding.prCircular.setVisibleToVisible()
//                            binding.scrollView.setVisibleToGone()
                        }

                        is UiState.Success -> {
                            isLogin()
                            currentUser = state.data
                            initView(state.data)
//                            binding.prCircular.setVisibleToGone()
//                            binding.scrollView.setVisibleToVisible()
                        }

                        is UiState.Error -> {
//                            binding.prCircular.setVisibleToError()
//                            binding.scrollView.setVisibleToGone()
                            clearUserInfo()
                            isLogOut()
                            Log.d("error", state.message)
                        }
                    }
                }
            }
        }
    }

    private fun initView(user: UserModel) {
        with(binding) {
            tvUserName.text = user.userName
            tvUserSelfIntroduction.text = user.userSelfIntroduction
            tvStackOfDevelopment.text = user.stackOfDevelopment
            tvPortfolio.text = user.userPortfolioText
            etUserName.setText(user.userName)
            etUserGithub.setText(user.userGithub)
            etUserSelfIntroduction.setText(user.userSelfIntroduction)
            etStackOfDevelopment.setText(user.stackOfDevelopment)
            etPortfolio.setText(user.userPortfolioText)
            setSelectedChips(binding.cgTypeTag, user.typeOfDevelopment)
            setSelectedChips(binding.cgProgramTag, user.programOfDevelopment)
            ivUserProfileImage.setThumbnailByUrlOrDefault(user.userProfileThumbnailUrl)
            ivBackProfileThumbnail.load(user.userBackgroundThumbnailUrl)
            ivPortfolioImage.setUploadImageByUrlOrDefault(user.userPortfolioImageUrl)
        }
    }

    private fun clearUserInfo() {
        with(binding) {
            tvUserName.text = ""
            etUserName.setText("")
            tvUserSelfIntroduction.text = ""
            etUserSelfIntroduction.setText("")
            etUserGithub.setText("")
            tvStackOfDevelopment.text = ""
            etStackOfDevelopment.setText("")
            tvPortfolio.text = ""
            etPortfolio.setText("")
            cgTypeTag.removeAllViews()
            cgProgramTag.removeAllViews()
            ivUserProfileImage.setThumbnailByUrlOrDefault(null)
            ivBackProfileThumbnail.setImageDrawable(null)
            ivPortfolioImage.setThumbnailByUrlOrDefault(null)
        }
    }

    private fun eachEventHandler() {
        binding.ivEditProfile.setOnClickListener {
            if (currentUser != null) {
                setChangeMode()
            } else {
                Snackbar.make(binding.root, "로그인 후 사용해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.btnCompleteEdit.setOnClickListener {
            if (validName()) {
                saveProfileInfo()
                setChangeMode()
                requireActivity().hideKeyboard()
            }
        }
        binding.ivEditProfileImage.setOnClickListener {
            pickProfileImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        binding.ivEditBackProfileThumbnail.setOnClickListener {
            pickBackgroundImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        binding.ivGitHub.setOnClickListener {
            val githubUrl = currentUser?.userGithub

            if (!githubUrl.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                startActivity(intent)
            } else {
                Snackbar.make(binding.root, "설정한 Github 주소가 없습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.ivSetUp.setOnClickListener {
            if (currentUser != null) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SetUpFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                Snackbar.make(binding.root, "로그인 후 사용해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.btnGoOnBoarding.setOnClickListener {
            val intent = Intent(requireActivity(), GetStartedActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setChangeMode() {
        isEditMode = !isEditMode

        val chipGroupDev = resources.getStringArray(R.array.chipGroupDevelopmentOccupations)
        val chipGroupLang = resources.getStringArray(R.array.chipProgramingLanguage)

        if (isEditMode) {
            setChipGroup(chipGroupDev, binding.cgTypeTag)
            setChipGroup(chipGroupLang, binding.cgProgramTag)
            selectChips(binding.cgTypeTag, currentUser?.typeOfDevelopment ?: emptyList())
            selectChips(binding.cgProgramTag, currentUser?.programOfDevelopment ?: emptyList())
        } else {
            setSelectedChips(binding.cgTypeTag, getChipText(binding.cgTypeTag))
            setSelectedChips(binding.cgProgramTag, getChipText(binding.cgProgramTag))
            currentUser?.let { initView(it) }
        }


        val editMode = arrayOf(
            binding.etUserName,
            binding.etUserGithub,
            binding.etUserSelfIntroduction,
            binding.etStackOfDevelopment,
            binding.etPortfolio,
            binding.btnCompleteEdit,
            binding.ivEditProfileImage,
            binding.ivEditBackProfileThumbnail
        )
        val viewMode = arrayOf(
            binding.ivEditProfile,
            binding.tvUserName,
            binding.tvUserSelfIntroduction,
            binding.tvStackOfDevelopment,
            binding.tvPortfolio,
            binding.ivGitHub
        )

        editMode.forEach { if (isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }
        viewMode.forEach { if (!isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }

        if (isEditMode) { //todo 임시
            binding.ivPortfolioImage.setOnClickListener {
                pickPortfolioImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
        } else {
            binding.ivPortfolioImage.setOnClickListener(null)
        }
    }

    private fun isLogin() { //todo 코드 간결화 필요
        binding.clUserDetailInfo.setVisibleToVisible()
        binding.ivSetUp.setVisibleToVisible()
        binding.ivEditProfile.setVisibleToVisible()
        binding.btnGoOnBoarding.setVisibleToGone()
    }

    private fun isLogOut() { //todo 코드 간결화 필요
        binding.clUserDetailInfo.setVisibleToGone()
        binding.ivSetUp.setVisibleToGone()
        binding.ivEditProfile.setVisibleToGone()
        binding.btnGoOnBoarding.setVisibleToVisible()
    }


    private fun createChip(text: String, isCheckable: Boolean): Chip {
        return Chip(requireContext()).apply {
            this.text = text
            this.isCheckable = isCheckable
            this.isCloseIconVisible = false
        }
    }

    private fun setChipGroup(chipList: Array<String>, chipGroup: ChipGroup) { //chip 설정
        chipGroup.removeAllViews()
        chipList.forEach {
            chipGroup.addView(createChip(it, true))
        }
    }

    private fun setSelectedChips(chipGroup: ChipGroup, selectedChips: List<String>) { //선택된 chip만 보여줌
        chipGroup.removeAllViews()
        selectedChips.forEach {
            chipGroup.addView(createChip(it, false))
        }
    }

    private fun selectChips(chipGroup: ChipGroup, selectedChips: List<String>) {
        chipGroup.children.forEach { chip ->
            if (chip is Chip && selectedChips.contains(chip.text.toString())) {
                chip.isChecked = true
            }
        }
    }

    private fun saveProfileInfo() {
        currentUser?.let { currentUser ->
            val updatedUserModel = currentUser.copy(
                userName = binding.etUserName.text.toString(),
                userSelfIntroduction = binding.etUserSelfIntroduction.text.toString(),
                userGithub = binding.etUserGithub.text.toString(),
                stackOfDevelopment = binding.etStackOfDevelopment.text.toString(),
                userPortfolioText = binding.etPortfolio.text.toString(),
                typeOfDevelopment = getChipText(binding.cgTypeTag),
                programOfDevelopment = getChipText(binding.cgProgramTag),
                userProfileThumbnailUrl = profileImageUri?.toString() ?: currentUser.userProfileThumbnailUrl,
                userBackgroundThumbnailUrl = backgroundImageUri?.toString() ?: currentUser.userBackgroundThumbnailUrl,
                userPortfolioImageUrl = portfolioImageUri?.toString() ?: currentUser.userPortfolioImageUrl
            )
            viewModel.saveUserProfile(updatedUserModel)
            sharedViewModel.updateUser(updatedUserModel)
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
            binding.etUserName.error = "이름을 입력해주세요."
            isValid = false
        } else if (!isValidName(name)) {
            binding.etUserName.error = "3-10자의 영문자나 한글만 가능합니다."
            isValid = false
        } else {
            binding.etUserName.error = null
        }

        return isValid
    }

    private fun isValidName(userName: String): Boolean {
        val usernamePattern = "^[A-Za-z가-힣]{3,20}$"
        return userName.matches(usernamePattern.toRegex())
    }
//    private fun isValidGitHubUrl(url: String): Boolean { //todo 깃헙주소 유효성 및 입력형태
//        val githubPattern = "^https://github\\.com/[A-Za-z0-9_-]+(/[A-Za-z0-9_-]+)*\$"
//        return url.matches(githubPattern.toRegex())
//    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}