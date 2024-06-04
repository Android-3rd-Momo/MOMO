package kr.nbc.momo.presentation.mypage

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.databinding.DialogAddTagBinding
import kr.nbc.momo.databinding.FragmentMyPageBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.signup.model.UserModel
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eachEventHandler()
//        sharedViewModel.getCurrentUser()
        observeUserProfileUpdate()
        observeUserProfile()

    }

    private fun observeUserProfileUpdate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            //todo
                        }

                        is UiState.Success -> {
                            sharedViewModel.getCurrentUser()
                        }

                        is UiState.Error -> {
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
            tvPortfolio.text = user.portfolio
            //편집모드에 값 적용
            etUserName.setText(user.userName)
            etUserSelfIntroduction.setText(user.userSelfIntroduction)
            etStackOfDevelopment.setText(user.stackOfDevelopment)
            etPortfolio.setText(user.portfolio)
            setChipList(binding.cgTypeTag, user.typeOfDevelopment)
            setChipList(binding.cgProgramTag, user.programOfDevelopment)
        }
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //fragment의 수명주기가 해당 상태일 때만 실행되도록 보장
                sharedViewModel.currentUser.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            //todo
                        }

                        is UiState.Success -> {
                            currentUser = state.data
                            initView(state.data)
                        }

                        is UiState.Error -> {
                            Log.d("error", state.message)
                        }
                    }
                }
            }
        }
    }

    private fun eachEventHandler() {
        binding.ivEditProfile.setOnClickListener {
            setChangeMode()
        }
        binding.tvAddTagTypeOfDevelopment.setOnClickListener {
            showAddTagDialog(binding.cgTypeTag)
        }
        binding.tvAddTagProgramOfDevelopment.setOnClickListener {
            showAddTagDialog(binding.cgProgramTag)
        }
        binding.btnCompleteEdit.setOnClickListener {
            saveProfileInfo()
            setChangeMode()
        }
    }

    private fun setChangeMode() {
        isEditMode = !isEditMode

        val editMode = arrayOf(
            binding.tvAddTagTypeOfDevelopment,
            binding.tvAddTagProgramOfDevelopment,
            binding.etUserName,
            binding.etUserSelfIntroduction,
            binding.tilStackOfDevelopment,
            binding.tilPortfolio,
            binding.btnCompleteEdit
        )
        val viewMode = arrayOf(
            binding.ivEditProfile,
            binding.tvUserName,
            binding.tvUserSelfIntroduction,
            binding.tvStackOfDevelopment,
            binding.tvPortfolio
        )

        editMode.forEach { if (isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }
        viewMode.forEach { if (!isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }
        setCloseIconVisibility(binding.cgTypeTag, isEditMode)
        setCloseIconVisibility(binding.cgProgramTag, isEditMode)
    }

    private fun setCloseIconVisibility(chipGroup: ChipGroup, visible: Boolean) {
        chipGroup.children.forEach { child ->
            (child as? Chip)?.isCloseIconVisible = visible
            child.isClickable = false
        }
    }

    private fun createChip(text: String, isCloseIcon: Boolean): Chip {
        return Chip(requireContext()).apply {
            this.text = text
            this.isCloseIconVisible = isCloseIcon
            setOnCloseIconClickListener {
                (parent as? ChipGroup)?.removeView(this)
            }
        }
    }

    private fun setChipList(chipGroup: ChipGroup, chipList: List<String>) {
        chipGroup.removeAllViews()
        chipList.forEach {
            chipGroup.addView(createChip(it, isEditMode))
        }
    }

    private fun showAddTagDialog(chipGroup: ChipGroup) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAddTagBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.buttonAdd.setOnClickListener {
            val tagText = dialogBinding.editTextTag.text.toString()
            if (tagText.isNotEmpty()) {
                addChipToGroup(chipGroup, tagText, true)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun addChipToGroup(chipGroup: ChipGroup, tagText: String, showCloseIcon: Boolean) {
        val chip = Chip(requireContext())
        chip.text = tagText
        chip.isClickable = false
        chip.isCloseIconVisible = showCloseIcon // x 생성
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip) // 선택한 아이템 삭제
        }
        chipGroup.addView(chip)
    }

    private fun saveProfileInfo() {
        currentUser?.let { currentUser ->
            val updatedUserModel = currentUser.copy(
                userName = binding.etUserName.text.toString(),
                userSelfIntroduction = binding.etUserSelfIntroduction.text.toString(),
                stackOfDevelopment = binding.etStackOfDevelopment.text.toString(),
                portfolio = binding.etPortfolio.text.toString(),
                typeOfDevelopment = getChipText(binding.cgTypeTag),
                programOfDevelopment = getChipText(binding.cgProgramTag)
            )
            viewModel.saveUserProfile(updatedUserModel)
        }
    }

    //    private fun saveProfileInfo() {
//        val currentUser = viewModel.getCurrentUser()
//        val updatedUserModel = currentUser.copy(
//            userName = binding.etUserName.text.toString(),
//            userSelfIntroduction = binding.etUserSelfIntroduction.text.toString(),
//            stackOfDevelopment = binding.etStackOfDevelopment.text.toString(),
//            portfolio = binding.etPortfolio.text.toString(),
//            typeOfDevelopment = getChipText(binding.cgTypeTag),
//            programOfDevelopment = getChipText(binding.cgProgramTag)
//        )
//        viewModel.saveUserProfile(updatedUserModel)
//    }
    //text를 list에 추가
    private fun getChipText(chipGroup: ChipGroup): List<String> {
        val textList = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            textList.add(chip.text.toString())
        }
        return textList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}