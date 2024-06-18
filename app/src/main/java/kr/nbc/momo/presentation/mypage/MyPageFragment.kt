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
import android.widget.Toast
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
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.setup.SetUpFragment
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

    //이미지 변경 상태 확인
    private var isProfileImageChange = false
    private var isBackgroundImageChange = false
    private var isPortfolioImageChange = false

    private var isEditMode = false
    private var currentUser: UserModel? = null
    private var profileImageUri: Uri? = null
    private var backgroundImageUri: Uri? = null
    private var portfolioImageUri: Uri? = null

    private val pickProfileImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            profileImageUri = uri
            isProfileImageChange = true
            binding.ivUserProfileImage.load(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val pickBackgroundImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            backgroundImageUri = uri
            isBackgroundImageChange = true
            binding.ivBackProfileThumbnail.load(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val pickPortfolioImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            portfolioImageUri = uri
            isPortfolioImageChange = true
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
                            Log.d("test", "loding")
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
            tvUserSelfIntroduction.text = user.userSelfIntroduction.ifEmpty { "자신을 소개해 보세요!" }
            tvStackOfDevelopment.text = user.stackOfDevelopment.ifEmpty { "사용하신 기술이 있다면 작성해 보세요!" }
            tvPortfolio.text = user.userPortfolioText.ifEmpty { "간단한 포트폴리오를 작성해 보세요!" }
            etUserName.setText(user.userName)
            etUserSelfIntroduction.setText(user.userSelfIntroduction)
            etStackOfDevelopment.setText(user.stackOfDevelopment)
            etPortfolio.setText(user.userPortfolioText)
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

        binding.etStackOfDevelopment.addTextWatcherWithError(500, "기술스택", binding.btnCompleteEdit, binding.tvCountStackEditText)
        binding.etPortfolio.addTextWatcherWithError(500, "포트폴리오", binding.btnCompleteEdit, binding.tvCountPortfolioEditText)
        binding.etUserSelfIntroduction.addTextWatcherWithError(60, "자기소개", binding.btnCompleteEdit)
    }

    private fun updateTextCount(et: EditText, tv: TextView) {
        val textLength = et.text.length
        val lengthText = "$textLength/500"
        tv.text = lengthText
    }

    private fun clearUserInfo() {
        with(binding) {
            tvUserName.text = ""
            etUserName.setText("")
            tvUserSelfIntroduction.text = ""
            etUserSelfIntroduction.setText("")
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
            setChangeMode()
        }
        binding.btnCompleteEdit.setOnClickListener {
            if (validName()) {
                saveProfileInfo()
                setChangeMode()
                requireActivity().hideKeyboard()
                observeUserProfileUpdate()
            }
        }
        binding.ivBack.setOnClickListener {
            setChangeMode()
            requireActivity().hideKeyboard()
        }
        binding.ivEditProfileImage.setOnClickListener {
            pickProfileImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        binding.ivEditBackProfileThumbnail.setOnClickListener {
            pickBackgroundImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        binding.ivSetUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SetUpFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnGoOnBoarding.setOnClickListener {
            val intent = Intent(requireActivity(), GetStartedActivity::class.java)
            startActivity(intent)
        }
        binding.ivDeleteProfileImage.setOnClickListener {
            profileImageUri = null
            isProfileImageChange = true
            binding.ivUserProfileImage.setThumbnailByUrlOrDefault(null)
        }
        binding.ivDeleteBackProfileThumbnail.setOnClickListener {
            backgroundImageUri = null
            isBackgroundImageChange = true
            binding.ivBackProfileThumbnail.setImageResource(R.color.blue)
        }
        binding.ivDeletePortfolioImage.setOnClickListener {
            portfolioImageUri = null
            isPortfolioImageChange = true
            binding.ivPortfolioImage.setUploadImageByUrlOrDefault(null)
        }
    }

    private fun setChangeMode() {
        isEditMode = !isEditMode

        val chipGroupDev = resources.getStringArray(R.array.chipGroupDevelopmentOccupations)
        val chipGroupLang = resources.getStringArray(R.array.chipProgramingLanguage)

        if (isEditMode) {
            setChipGroup(chipGroupDev, binding.cgTypeTag, isEditMode)
            setChipGroup(chipGroupLang, binding.cgProgramTag, isEditMode)
            selectChips(binding.cgTypeTag, currentUser?.typeOfDevelopment ?: emptyList())
            selectChips(binding.cgProgramTag, currentUser?.programOfDevelopment ?: emptyList())
            binding.tvEmptyTypeTag.setVisibleToGone()
            binding.tvEmptyProgramTag.setVisibleToGone()
            binding.cgTypeTag.setVisibleToVisible()
            binding.cgProgramTag.setVisibleToVisible()
            updateTextCount(binding.etStackOfDevelopment, binding.tvCountStackEditText)
            updateTextCount(binding.etPortfolio, binding.tvCountPortfolioEditText)

            binding.ivPortfolioImage.setOnClickListener {
                pickPortfolioImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
            binding.llProfileImage.setBackgroundResource(R.drawable.circle_blue)
            hideNav()
            val rootView = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
            rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        } else {
            //태그 빈값
            if (binding.cgTypeTag.childCount == 0) {
                binding.tvEmptyTypeTag.setVisibleToVisible()
                binding.cgTypeTag.setVisibleToGone()
            } else {
                binding.tvEmptyTypeTag.setVisibleToGone()
            }
            if (binding.cgProgramTag.childCount == 0) {
                binding.tvEmptyProgramTag.setVisibleToVisible()
                binding.cgProgramTag.setVisibleToGone()
            } else {
                binding.tvEmptyProgramTag.setVisibleToGone()
            }

            setSelectedChips(binding.cgTypeTag, getChipText(binding.cgTypeTag))
            setSelectedChips(binding.cgProgramTag, getChipText(binding.cgProgramTag))
            currentUser?.let { initView(it) }

            binding.ivPortfolioImage.setOnClickListener(null)
            binding.llProfileImage.setBackgroundResource(0)
            showNav()
            val rootView = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }


        val editMode = arrayOf(
            binding.etUserName,
            binding.etUserSelfIntroduction,
            binding.etStackOfDevelopment,
            binding.etPortfolio,
            binding.btnCompleteEdit,
            binding.ivEditProfileImage,
            binding.ivEditBackProfileThumbnail,
            binding.cvEditProfileImage,
            binding.tvCountStackEditText,
            binding.tvCountPortfolioEditText,
            binding.ivBack,
            binding.cvDeleteProfileImage,
            binding.ivDeleteBackProfileThumbnail,
            binding.ivDeletePortfolioImage

        )
        val viewMode = arrayOf(
            binding.ivEditProfile,
            binding.tvUserName,
            binding.tvUserSelfIntroduction,
            binding.tvStackOfDevelopment,
            binding.tvPortfolio,
        )

        editMode.forEach { if (isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }
        viewMode.forEach { if (!isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }
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
        binding.tvUserName.text = "로그인이 필요합니다"
        binding.btnGoOnBoarding.setVisibleToVisible()
    }


    private fun createChip(text: String, isEditMode: Boolean): Chip {
        return Chip(requireContext()).apply {
            this.text = text
            this.isCheckable = true
            this.isCloseIconVisible = false
            this.isClickable = isEditMode
            updateChipAppearance(this, isChecked)

            setOnCheckedChangeListener { _, isChecked ->
                if (isEditMode) {
                    updateChipAppearance(this, isChecked)
                }
            }
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

    private fun setChipGroup(chipList: Array<String>, chipGroup: ChipGroup, isEditMode: Boolean) {
        chipGroup.removeAllViews()
        for (chipText in chipList) {
            val chip = createChip(chipText, isEditMode)
            chipGroup.addView(chip)
        }
    }

    private fun setSelectedChips(chipGroup: ChipGroup, selectedChips: List<String>) { //선택된 chip만 보여줌
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

    private fun selectChips(chipGroup: ChipGroup, selectedChips: List<String>) {
        chipGroup.children.forEach { chip ->
            if (chip is Chip) {
                chip.isChecked = selectedChips.contains(chip.text.toString())
                updateChipAppearance(chip, chip.isChecked)
                chip.isClickable = isEditMode
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
                userProfileThumbnailUrl = if (isProfileImageChange) profileImageUri?.toString() ?: ""
                else currentUser.userProfileThumbnailUrl,
                userBackgroundThumbnailUrl = if (isBackgroundImageChange) backgroundImageUri?.toString() ?: ""
                else currentUser.userBackgroundThumbnailUrl,
                userPortfolioImageUrl = if (isPortfolioImageChange) portfolioImageUri?.toString() ?: ""
                else currentUser.userPortfolioImageUrl
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

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rootView = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.rootView.height
        val keypadHeight = screenHeight - rect.bottom
        if (keypadHeight > screenHeight * 0.15) {
            binding.btnCompleteEdit.visibility = View.GONE
        } else {
            binding.btnCompleteEdit.visibility = View.VISIBLE
        }
    }

    private fun showNav() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav.setVisibleToVisible()
    }

    private fun hideNav() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav.setVisibleToGone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}