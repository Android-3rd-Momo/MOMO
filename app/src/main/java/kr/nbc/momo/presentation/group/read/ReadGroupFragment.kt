
package kr.nbc.momo.presentation.group.read

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.DialogJoinProjectBinding
import kr.nbc.momo.databinding.DialogSelectNumberBinding
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattingroom.ChattingRoomFragment
import kr.nbc.momo.presentation.group.model.CategoryModel
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.presentation.userinfo.UserInfoFragment
import kr.nbc.momo.util.addTextWatcherWithError
import kr.nbc.momo.util.makeToastWithStringRes
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible
import java.util.Calendar

@AndroidEntryPoint
class ReadGroupFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: String? = null
    private var categoryText: String = ""
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
        observeUserList()
        observeDeleteGroup()
        observeBlockUser()
        observeReportUser()
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
        bottomNavHide()
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomNavShow()
        _binding = null
    }

    private fun bottomNavHide() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.setVisibleToGone()
    }

    private fun bottomNavShow() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.setVisibleToVisible()
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
                            if (uiState.data != null) {
                                Log.d("currentUser", uiState.data.userId)
                                currentUser = uiState.data.userId
                                initGroup()
                            }else{
                                initGroup()
                            }
                        }

                        is UiState.Error -> {
                            Log.d("error", uiState.message)
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
                        binding.prCircular.setVisibleToError()
                        binding.svRead.setVisibleToGone()
                    }
                }

            }
        }
    }


    private fun observeUserList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.subscriptionState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        makeToastWithStringRes(requireContext(), R.string.apply_success)
                        //Toast.makeText(requireContext(), "가입 신청 성공", Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Error -> {
                        // 오류 메시지 표시
                        Log.d("error", uiState.message)
                    }
                }

            }
        }
    }

    private fun observeDeleteGroup() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteGroupState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 로딩 처리 (필요한 경우)
                    }

                    is UiState.Success -> {
                        parentFragmentManager.popBackStack()
                        makeToastWithStringRes(requireContext(), R.string.delete_group_success)
//                        Toast.makeText(requireContext(), getString(R.string.delete_group_success), Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Error -> {
                        Log.d("error", uiState.message)
                    }
                }
            }
        }
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
//                        Toast.makeText(requireContext(), getString(R.string.user_report_success), Toast.LENGTH_SHORT).show()
                        makeToastWithStringRes(requireContext(), R.string.user_report_success)
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


    private fun initGroup() {
        lifecycleScope.launch {
            sharedViewModel.groupId.collectLatest { groupId ->
                if (groupId != null) {
                    viewModel.readGroup(groupId)
                }
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

            if (data.userList.contains(currentUser)) btnJoinProject.setText(R.string.move_to_chatting)
            if (data.leaderId == currentUser) {
                btnEdit.setVisibleToVisible()
                btnPopUp.setVisibleToGone()
            }

            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }


            initUserList(data.userList)
            btnJoinProjectClickListener(currentUser, data)
            btnEditClickListener()
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

    private fun initGroupThumbnail(groupThumbnail: String?) {
        if (groupThumbnail.isNullOrEmpty()) {
            binding.ivGroupImage.setThumbnailByUrlOrDefault(null)
        } else {
            binding.ivGroupImage.setThumbnailByUrlOrDefault(groupThumbnail)
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
                val userInfoFragment = UserInfoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, userInfoFragment)
                    .addToBackStack(null)
                    .commit()
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
                    //sharedViewModel.getChattingListById(sharedViewModel.groupId.value?:"")
                    val chattingRoomFragment = ChattingRoomFragment()
                    parentFragmentManager.popBackStack()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, chattingRoomFragment)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                binding.btnJoinProject.setOnClickListener {
                    if (data.userList.size < data.limitPerson.toInt()) {
                        showDialog(true, data, currentUser)
                    } else {
                        makeToastWithStringRes(requireContext(), R.string.over_max_user)
//                        Toast.makeText(requireContext(), getString(R.string.over_max_user), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun btnEditClickListener() {
        binding.btnEdit.setOnClickListener {
            val editReadGroupFragment = EditReadGroupFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editReadGroupFragment)
                .addToBackStack("Read")
                .commit()
        }

        if (currentUser != null) {
            binding.btnPopUp.setOnClickListener {
                showPopup(binding.btnPopUp)
            }
        } else {
            binding.btnPopUp.setOnClickListener{
                makeToastWithStringRes(requireContext(), R.string.need_login)
//                Toast.makeText(requireContext(), getString(R.string.need_login), Toast.LENGTH_SHORT).show()
            }
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
                    if (currentUser != null) {
                        viewModel.subscription(currentUser, data.groupId)
                    }
                    //viewModel.joinGroup(data.groupId)

                }
            }
        } else {
            dialogBinding.tvClose.setText(R.string.go_to_login)
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(requireActivity(), GetStartedActivity::class.java)
                startActivity(intent)
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showPopup(v: View) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu1 -> {
                viewModel.deleteGroup(groupId, userList)
            }

            R.id.menu2 -> {
                viewModel.reportUser(leaderId)
                viewModel.blockUser(leaderId)
            }

            R.id.menu3 -> {
                viewModel.blockUser(leaderId)
            }
        }

        return item != null
    }
}