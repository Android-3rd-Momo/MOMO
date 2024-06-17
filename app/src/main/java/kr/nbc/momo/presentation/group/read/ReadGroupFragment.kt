
package kr.nbc.momo.presentation.group.read

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import kr.nbc.momo.presentation.onboarding.signup.SignUpFragment
import kr.nbc.momo.presentation.userinfo.UserInfoFragment
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
    private var isEditMode = false
    private var imageUri: Uri? = null
    private var image: String? = null
    private var categoryText: String = "공모전"
    private var groupLimitPeople: String = ""
    private var groupId: String = ""
    private var leaderId: String = ""
    private var userList: List<String> = listOf()
    private var firstMinTimeInMillis: Long = System.currentTimeMillis() + 1
    private var firstMaxTimeInMillis: Long = System.currentTimeMillis() + 2592000000 // 현재 시간 + 한달뒤
    private var lastMinTimeInMillis: Long = System.currentTimeMillis() + 1
    private var lastMaxTimeInMillis: Long = System.currentTimeMillis() + 2592000000 // 현재 시간 + 한달뒤
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivGroupImage.setThumbnailByUrlOrDefault(uri.toString())
            binding.ivGroupImageEdit.setThumbnailByUrlOrDefault(uri.toString())
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

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
        observeUpdateState()
        observeUserList()
        observeDeleteGroup()
        observeBlockUser()
        observeReportUser()
        observeChangeLeader()
        observeDeleteUser()
        initTextWatcher()
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

    private fun observeUpdateState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        initView(uiState.data)
                        initUserList(uiState.data.userList)
                    }

                    is UiState.Error -> {
                        initGroupThumbnail(image)
                    }
                }
            }
        }
    }

    private fun observeUserList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userListState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        initUserList(uiState.data)
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
                        Toast.makeText(requireContext(), "게시글 삭제 성공", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), "유저 신고 성공", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), "유저 차단 성공", Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Error -> {
                        Log.d("error", uiState.message)
                    }
                }

            }
        }
    }

    private fun observeChangeLeader() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.leaderChangeState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 로딩 처리 (필요한 경우)
                    }

                    is UiState.Success -> {
                        Toast.makeText(requireContext(), "리더 변경 성공", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }

                    is UiState.Error -> {
                        Log.d("error", uiState.message)
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
                        Toast.makeText(requireContext(), "유저 강퇴 성공", Toast.LENGTH_SHORT).show()
                        initUserList(uiState.data)

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
            etGroupNameEdit.setText(data.groupName)
            etGroupOneLineDescriptionEdit.setText(data.groupOneLineDescription)
            etGroupDescriptionEdit.setText(data.groupDescription)
            ivGroupImageEdit.setThumbnailByUrlOrDefault(data.groupThumbnail)
            tvLeaderIdEdit.text = data.leaderId
            tvFirstDateEdit.text = data.firstDate
            tvLastDateEdit.text = data.lastDate

            val limitPeopleText = data.userList.size.toString() + "/" + data.limitPerson
            tvLimitPeople.text = limitPeopleText

            initChip(chipGroupDevelopmentOccupations, data.category.developmentOccupations)
            initChip(chipProgramingLanguage, data.category.programingLanguage)

            if (data.userList.contains(currentUser)) btnJoinProject.text = "채팅방 이동"
            if (data.leaderId == currentUser) {
                btnEdit.setVisibleToVisible()
                btnPopUp.setVisibleToGone()
            }

            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }


            initUserList(data.userList)
            btnJoinProjectClickListener(currentUser, data)
            btnEditClickListener(data)
        }
        initSpinner(data.category.classification)
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
        binding.ivGroupImage.setThumbnailByUrlOrDefault(groupThumbnail)
        binding.ivGroupImageEdit.setThumbnailByUrlOrDefault(groupThumbnail)
    }

    private fun initUserList(userList: List<String>) {
        val adapter = UserListAdapter(userList, leaderId, requireContext())
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

        val editAdapter = EditUserListAdapter(userList, leaderId, requireContext())
        binding.rvUserListEdit.adapter = editAdapter
        binding.rvUserListEdit.layoutManager = GridLayoutManager(requireContext(), 2)
        editAdapter.longClick = object : EditUserListAdapter.LongClick {
            override fun longClick(userId: String) {
                val dailog = Dailog.LeaderChange
                showDialog(groupId, userId, dailog)
            }
        }

        editAdapter.onClick = object : EditUserListAdapter.OnClick {
            override fun onClick(userId: String) {
                val dailog = Dailog.DeleteUser
                showDialog(groupId, userId, dailog)
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
                        Toast.makeText(requireContext(), "참가 인원 수 초과", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun btnEditClickListener(data: GroupModel) {
        binding.btnEdit.setOnClickListener {
            setEditMode(data)
        }

        binding.btnPopUp.setOnClickListener {
            showPopup(binding.btnPopUp)
        }
    }

    private fun setEditMode(data: GroupModel) {
        val categoryList = data.category.programingLanguage + data.category.developmentOccupations
        val chipGroupDev = resources.getStringArray(R.array.chipGroupDevelopmentOccupations)
        val chipGroupLang = resources.getStringArray(R.array.chipProgramingLanguage)
        setChipGroup(chipGroupDev, binding.chipGroupDevelopmentOccupationsEdit, categoryList)
        setChipGroup(chipGroupLang, binding.chipProgramingLanguageEdit, categoryList)

        val editMode = arrayOf(
            binding.clEditMode,
            binding.clSimpleDescriptionContainerEdit,
            binding.btnDelete
        )
        val viewMode = arrayOf(
            binding.clViewMode,
            binding.clSimpleDescriptionContainer,
            binding.btnEdit
        )

        val limitPeopleText = data.userList.size.toString() + "/" + data.limitPerson
        binding.tvLimitPeopleEdit.text = limitPeopleText
        binding.tvLimitPeopleEdit.setOnClickListener {
            showDialogNumberPicker(binding.tvLimitPeopleEdit, data.userList.size)
        }

        binding.ivGroupImageEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        binding.tvFirstDateEdit.setOnClickListener {
            showDialog(binding.tvFirstDateEdit, Value.First)
        }
        binding.tvLastDateEdit.setOnClickListener {
            showDialog(binding.tvLastDateEdit, Value.Last)
        }
        binding.btnCompleteEdit.setOnClickListener {
            btnCompleteEditOnClickListener(data, editMode, viewMode)
        }
        binding.btnDelete.setOnClickListener {
            viewModel.deleteGroup(data.groupId, data.userList)
        }
        setChangeMode(editMode, viewMode)
    }

    private fun initSpinner(category: String) {
        val items = resources.getStringArray(R.array.classification)
        val spinnerAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_item_category) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)

                if (position == count) {
                    val textView = (v.findViewById<View>(R.id.tvCategorySpinner) as TextView)
                    textView.text = getItem(count)
                }

                return v
            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }

        spinnerAdapter.addAll(items.toMutableList())
        spinnerAdapter.add(category)
        binding.categorySpinner.adapter = spinnerAdapter
        binding.categorySpinner.setSelection(spinnerAdapter.count)
        binding.categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p0 != null) {
                        categoryText = p0.getItemAtPosition(p2).toString()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    return
                }
            }
    }

    private fun setChangeMode(editMode: Array<View>, viewMode: Array<View>) {
        isEditMode = !isEditMode
        editMode.forEach { if (isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }
        viewMode.forEach { if (!isEditMode) it.setVisibleToVisible() else it.setVisibleToGone() }
    }

    private fun btnCompleteEditOnClickListener(data: GroupModel, editMode: Array<View>, viewMode: Array<View>) {
        val categoryList = CategoryModel(
            categoryText,
            getChipText(binding.chipGroupDevelopmentOccupationsEdit),
            getChipText(binding.chipProgramingLanguageEdit)
        )
        image = data.groupThumbnail
        viewModel.updateGroup(
            data.copy(
                groupName = binding.etGroupNameEdit.text.toString(),
                groupOneLineDescription = binding.etGroupOneLineDescriptionEdit.text.toString(),
                groupDescription = binding.etGroupDescriptionEdit.text.toString(),
                firstDate = binding.tvFirstDateEdit.text.toString(),
                lastDate = binding.tvLastDateEdit.text.toString(),
                category = categoryList,
                limitPerson = groupLimitPeople
            ), imageUri
        )
        setChangeMode(editMode, viewMode)
    }

    private fun showDialogNumberPicker(textView: TextView, size: Int) {
        val arr =  Array(100) { (it + 5).toString() }
        val dialogBinding = DialogSelectNumberBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.numberPicker.minValue = 5
        dialogBinding.numberPicker.maxValue = arr.size
        dialogBinding.numberPicker.displayedValues = arr
        dialogBuilder.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnConfirm.setOnClickListener {
            dialogBuilder.dismiss()
            val limitPeopleText = size.toString() + "/" + dialogBinding.numberPicker.value
            textView.text = limitPeopleText
            groupLimitPeople = dialogBinding.numberPicker.value.toString()
        }
        dialogBuilder.show()
    }

    private fun showDialog(dateType: TextView, value: Value) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            val mon = month + 1
            val monthText = if (mon < 10) {
                "0$mon"
            } else mon.toString()

            val dayText = if (day < 10) {
                "0$day"
            } else day.toString()

            dateType.text = "$year.$monthText.$dayText"

            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day, 0, 0, 0)
            selectedCalendar.set(Calendar.MILLISECOND, 0)

            // 선택 후
            if (value == Value.First) {
                lastMinTimeInMillis = selectedCalendar.timeInMillis
            } else if (value == Value.Last) {
                firstMaxTimeInMillis = selectedCalendar.timeInMillis
            }
        }
        var picker = DatePickerDialog(requireContext(), listener, year, month, day)

        // 선택 전
        if (value == Value.First) {
            picker.datePicker.minDate = firstMinTimeInMillis
            picker.datePicker.maxDate = firstMaxTimeInMillis
        } else if (value == Value.Last) {
            picker.datePicker.minDate = lastMinTimeInMillis
            picker.datePicker.maxDate = lastMaxTimeInMillis
        }
        picker.show()
    }

    private fun showDialog(groupId: String, userId: String, anDailog: Dailog) {
        val dialogBinding = DialogJoinProjectBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialogBuilder.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (anDailog == Dailog.LeaderChange) {
            dialogBinding.tvClose.text = "리더를 $userId 님으로 변경합니다."
            dialogBinding.btnConfirm.setOnClickListener {
                dialogBuilder.dismiss()
                viewModel.leaderChange(groupId, userId)

            }
        }

        if (anDailog == Dailog.DeleteUser) {
            dialogBinding.tvClose.text = "$userId 님을 모임에서 추방합니다."
            dialogBinding.btnConfirm.setOnClickListener {
                dialogBuilder.dismiss()
                viewModel.deleteUser(userId, groupId)

            }
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialogBuilder.dismiss()
        }
        dialogBuilder.show()
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
            dialogBinding.tvClose.text = "모임에 참여하시겠습니까?"
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                lifecycleScope.launch {
                    val list = data.userList.toMutableList()
                    list.add(currentUser!!)
                    viewModel.addUser(list, data.groupId)
                    viewModel.joinGroup(data.groupId)
                    initView(data.copy(userList = list))
                }
            }
        } else {
            dialogBinding.tvClose.text = "로그인페이지로 이동합니다."
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                val signUpFragment = SignUpFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, signUpFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setChipGroup(chipList: Array<String>, chipGroup: ChipGroup, category: List<String>) {
        if (chipGroup.childCount == 0) {
            for (chipText in chipList) {
                val chip = Chip(requireContext()).apply {
                    text = chipText
                    setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.tv_chip_state_color
                        )
                    )
                    setChipBackgroundColorResource(R.color.bg_chip_state_color)
                    isCheckable = true
                    if (category.contains(chipText)) {
                        isChecked = true
                    }
                }
                chipGroup.addView(chip)
            }
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

    private fun initTextWatcher(){
        with(binding){
            etGroupDescriptionEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //No action needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val textLength = binding.etGroupDescriptionEdit.text.length
                    if (textLength > 500) {
                        binding.etGroupDescriptionEdit.error = "그룹 소개는 500자까지 작성 가능합니다."
                        btnCompleteEdit.isEnabled = false
                    } else {
                        binding.etGroupDescriptionEdit.error = null
                        btnCompleteEdit.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //No action needed
                }

            })

            etGroupOneLineDescriptionEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //No action needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val textLength = binding.etGroupOneLineDescriptionEdit.text.length
                    if (textLength > 30) {
                        binding.etGroupOneLineDescriptionEdit.error = "그룹 한 줄 소개는 30자까지 작성 가능합니다."
                        btnCompleteEdit.isEnabled = false
                    } else {
                        binding.etGroupOneLineDescriptionEdit.error = null
                        btnCompleteEdit.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //No action needed
                }

            })

            etGroupNameEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //No action needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val textLength = binding.etGroupNameEdit.text.length
                    if (textLength > 30) {
                        binding.etGroupNameEdit.error = "그룹 이름은 30자까지 작성 가능합니다."
                        btnCompleteEdit.isEnabled = false
                    } else {
                        binding.etGroupNameEdit.error = null
                        btnCompleteEdit.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //No action needed
                }

            })
        }
    }
}