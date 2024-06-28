
package kr.nbc.momo.presentation.group.read

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.DialogJoinProjectBinding
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.OnBoardingActivity
import kr.nbc.momo.util.hideNav
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.makeToastWithStringRes
import kr.nbc.momo.util.setGroupImageByUrlOrDefault
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible
import kr.nbc.momo.util.showNav

@AndroidEntryPoint
class ReadGroupFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: String? = null
    private var groupLimitPeople: String = ""
    private var groupId: String = ""
    private var leaderId: String = ""
    private var userList: List<String> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGroupState()
        observeUserProfile()
        observeDeleteUser()
        btnSetOnclickListener()
    }

    override fun onStart() {
        super.onStart()
        if (leaderId == currentUser) {
            binding.btnEdit.setVisibleToVisible()
            binding.btnPopUp.setVisibleToGone()
        }
    }

    override fun onResume() {
        super.onResume()
        hideNav()
    }

    override fun onDestroy() {
        super.onDestroy()
        showNav()
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
                            initGroup()
/*                            if (uiState.data != null) {
                                Log.d("currentUser", uiState.data.userId)
                                currentUser = uiState.data.userId
                                initGroup()
                            }else{
                                initGroup()
                            }*/
                        }

                        is UiState.Error -> {
                            Log.d("error", uiState.message)
                            makeToastWithString(requireContext(), uiState.message)
                            initGroup()
                        }
                    }
                }
            }
        }
    }


    private fun observeGroupState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.groupState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        binding.prCircular.setVisibleToVisible()
                        binding.svRead.setVisibleToGone()
                    }

                    is UiState.Success -> {
                        groupId = uiState.data.groupId
                        leaderId = uiState.data.leaderId
                        userList = uiState.data.userList
                        groupLimitPeople = uiState.data.limitPerson
                        binding.prCircular.setVisibleToGone()
                        binding.svRead.setVisibleToVisible()
                        initView(uiState.data)
                        initGroupThumbnail(uiState.data.groupThumbnail)
                        initUserList(uiState.data.userList)
                    }

                    is UiState.Error -> {
                        // 오류 메시지 표시
                        Log.d("error", uiState.message)
                        makeToastWithString(requireContext(), uiState.message)
                        binding.prCircular.setVisibleToError()
                        binding.svRead.setVisibleToGone()
                    }
                }

            }
        }
    }

    private fun observeDeleteUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userDeleteState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 로딩 처리 (필요한 경우)
                    }

                    is UiState.Success -> {
                        makeToastWithStringRes(requireContext(), R.string.exit_group_success)
                        initUserList(uiState.data)
                        initBtnJoin()

                    }

                    is UiState.Error -> {
                        makeToastWithString(requireContext(), uiState.message)
                        Log.d("error", uiState.message)
                    }
                }

            }
        }
    }

    private fun initGroup() {
        lifecycleScope.launch {
            sharedViewModel.groupId.collectLatest { groupId ->
                groupId?.let {
                    viewModel.readGroup(it)
                }
/*                if (groupId != null) {
                    viewModel.readGroup(groupId)
                }*/
            }
        }
    }

    private fun initView(data: GroupModel) {
        with(binding) {
            ivGroupImage.clipToOutline = true
            tvCategoryClassification.text = data.category.classification
            tvGroupName.text = data.groupName
            tvGroupOneLineDescription.text = data.groupOneLineDescription
            tvGroupDescription.text = data.groupDescription
            tvFirstDate.text = data.firstDate
            tvLastDate.text = data.lastDate
            tvLeaderId.text = data.leaderId

            val limitPeopleText = data.userList.size.toString() + "/" + data.limitPerson
            tvLimitPeople.text = limitPeopleText

            initChip(chipGroupDevelopmentOccupations, data.category.developmentOccupations)
            initChip(chipProgramingLanguage, data.category.programingLanguage)

            initBtnJoin()

            if (userList.contains(currentUser)) {
                if (data.leaderId == currentUser) {
                    btnEdit.setVisibleToVisible()
                    btnPopUp.setVisibleToGone()
                    tvExit.setVisibleToGone()
                } else {
                    btnEdit.setVisibleToGone()
                    btnPopUp.setVisibleToGone()
                    tvExit.setVisibleToVisible()
                }
            } else {
                btnEdit.setVisibleToGone()
                btnPopUp.setVisibleToVisible()
                tvExit.setVisibleToGone()
            }


            initUserList(data.userList)
            btnJoinProjectClickListener(currentUser, data)
            btnEditClickListener()
            tvExitClickListener()
        }
    }

    private fun initBtnJoin() {
        if (userList.contains(currentUser)) {
            binding.btnJoinProject.setText(R.string.move_to_chatting)
        } else {
            binding.btnJoinProject.setText(R.string.join_group)
        }
    }

    private fun btnSetOnclickListener() {
        binding.ivReturn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun initChip(chipGroup: ChipGroup, chipList: List<String>) {
        chipGroup.removeAllViews()
        for (chipText in chipList) {
            val chip = Chip(requireContext()).apply {
                text = chipText
                setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.base_chip_text
                    )
                )
                setChipBackgroundColorResource(
                    R.color.base_chip_bg
                )
            }
            chipGroup.addView(chip)
        }
    }

    private fun initGroupThumbnail(groupThumbnail: String?) {
        if (groupThumbnail.isNullOrEmpty()) {
            binding.ivGroupImage.setGroupImageByUrlOrDefault(null)
        } else {
            binding.ivGroupImage.setGroupImageByUrlOrDefault(groupThumbnail)
        }
    }

    private fun initUserList(userList: List<String>) {
        val userMutableList = userList.toMutableList()
        val index = userMutableList.indexOf(leaderId)
        if (index != -1) {
            userMutableList.removeAt(index)
            userMutableList.add(0, leaderId)
        }

        val adapter = UserListAdapter(userMutableList, leaderId, requireContext())
        binding.rvUserList.adapter = adapter
        binding.rvUserList.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter.itemClick = object : UserListAdapter.ItemClick {
            override fun itemClick(userId: String) {
                sharedViewModel.getUserId(userId)
                findNavController().navigate(R.id.action_readGroupFragment_to_userInfoFragment)
            }
        }
    }

    private fun btnJoinProjectClickListener(currentUser: String?, data: GroupModel) {
        if (currentUser == null) {
            binding.btnJoinProject.setOnClickListener {
                showDialog(false, data, currentUser)
            }
        } else {
            if (data.userList.contains(currentUser)) {
                binding.btnJoinProject.setOnClickListener {
                    findNavController().navigate(R.id.action_readGroupFragment_to_chattingRoomFragment)
                }
            } else {
                binding.btnJoinProject.setOnClickListener {
                    if (data.userList.size < data.limitPerson.toInt()) {
                        showDialog(true, data, currentUser)
                    } else {
                        makeToastWithStringRes(requireContext(), R.string.over_max_user)
                    }
                }
            }
        }
    }

    private fun btnEditClickListener() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_readGroupFragment_to_editReadGroupFragment)
        }

        if (currentUser != null) {
            binding.btnPopUp.setOnClickListener {
                showPopup(binding.btnPopUp)
            }
        } else {
            binding.btnPopUp.setOnClickListener{
                makeToastWithStringRes(requireContext(), R.string.need_login)
            }
        }
    }

    private fun tvExitClickListener() {
        binding.tvExit.setOnClickListener {

            val dialogBinding = DialogJoinProjectBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()

            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.tvClose.setText(R.string.exit_group)
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()

                currentUser?.let { it1 -> viewModel.deleteUser(it1, groupId) }
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }



    private fun showDialog(loginBoolean: Boolean, data: GroupModel, currentUser: String?) {
        val dialogBinding = DialogJoinProjectBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (loginBoolean) {
            dialogBinding.tvClose.setText(R.string.ask_join_group)
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                lifecycleScope.launch {
                    try {
                        if (currentUser != null) {
                            viewModel.subscription(currentUser, data.groupId)
                            makeToastWithStringRes(requireContext(), R.string.subscription_group_success)
                        }
                    } catch (e : Exception) {
                        makeToastWithStringRes(requireContext(), R.string.error)
                    }
                }
            }
        } else {
            dialogBinding.tvClose.setText(R.string.go_to_login)
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(requireActivity(), OnBoardingActivity::class.java)
                startActivity(intent)
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showPopup(v: View) {
        val popup = PopupMenu(requireContext(), v, 0, 0, R.style.CustomPopupMenu)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.report_group -> {
                try {
                    viewModel.deleteGroup(groupId, userList)
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    makeToastWithStringRes(requireContext(), R.string.error)
                }
            }

            R.id.report_user -> {
                try {
                    viewModel.reportUser(leaderId)
                    viewModel.blockUser(leaderId)
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    makeToastWithStringRes(requireContext(), R.string.error)
                }

            }

            R.id.block_user -> {
                try {
                    viewModel.blockUser(leaderId)
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    makeToastWithStringRes(requireContext(), R.string.error)
                }
            }
        }

        return item != null
    }
}