package kr.nbc.momo.presentation.group.read

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.DialogJoinProjectBinding
import kr.nbc.momo.databinding.DialogSelectNumberBinding
import kr.nbc.momo.databinding.FragmentEditReadGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.CategoryModel
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.addTextWatcherWithError
import kr.nbc.momo.util.getAfterOneMonthTimeMillis
import kr.nbc.momo.util.getCurrentTimeMillis
import kr.nbc.momo.util.makeToastWithStringRes
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setVisibleToVisible
import java.util.Calendar

@AndroidEntryPoint
class EditReadGroupFragment : Fragment() {
    private var _binding: FragmentEditReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditReadGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: String? = null
    private var isGroupImageChange = false
    private var categoryText: String = ""
    private var imageUri: Uri? = null
    private var groupId: String = ""
    private var leaderId: String = ""
    private var firstMinTimeInMillis: Long = getCurrentTimeMillis()
    private var firstMaxTimeInMillis: Long = getAfterOneMonthTimeMillis()
    private var lastMinTimeInMillis: Long = getCurrentTimeMillis()
    private var lastMaxTimeInMillis: Long = getAfterOneMonthTimeMillis()
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            isGroupImageChange = true
            binding.ivGroupImageEdit.setThumbnailByUrlOrDefault(uri.toString())
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReadGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGroupState()
        observeUserProfile()
        observeDeleteUser()
        initTextWatcher()
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
                            /*
                            if (uiState.data != null) {
                                Log.d("currentUser", uiState.data.userId)
                                currentUser = uiState.data.userId
                                initGroup()
                            }else{
                                initGroup()
                            }*/
                            initGroup()
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

                    }

                    is UiState.Success -> {
                        groupId = uiState.data.groupId
                        leaderId = uiState.data.leaderId
                        binding.svRead.setVisibleToVisible()
                        initView(uiState.data)
                        initUserList(uiState.data.userList)
                    }

                    is UiState.Error -> {

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
                        makeToastWithStringRes(requireContext(), R.string.user_ban_success)
//                        Toast.makeText(requireContext(), getString(R.string.user_block_success), Toast.LENGTH_SHORT).show()
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
                groupId?.let {
                    viewModel.readGroup(it)
                }
/*                if (groupId != null) {
                    viewModel.readGroup(groupId)
                }*/
            }
        }
    }


    private fun initUserList(userList: List<String>) {
        val userMutableList = userList.toMutableList()
        val index = userMutableList.indexOf(leaderId)
        if (index != -1) {
            userMutableList.removeAt(index)
            userMutableList.add(0, leaderId)
        }
        val editAdapter = EditUserListAdapter(userMutableList, leaderId, requireContext())
        binding.rvUserListEdit.adapter = editAdapter
        binding.rvUserListEdit.layoutManager = GridLayoutManager(requireContext(), 2)
        editAdapter.longClick = object : EditUserListAdapter.LongClick {
            override fun longClick(userId: String) {
                val enumDialog = EnumDialog.LeaderChange
                showDialog(groupId, userId, enumDialog)
            }
        }

        editAdapter.onClick = object : EditUserListAdapter.OnClick {
            override fun onClick(userId: String) {
                val enumDialog = EnumDialog.DeleteUser
                showDialog(groupId, userId, enumDialog)
            }
        }
    }

    private fun initView(data: GroupModel) {
        with(binding) {

            val categoryList = data.category.programingLanguage + data.category.developmentOccupations
            val chipGroupDev = resources.getStringArray(R.array.chipGroupDevelopmentOccupations)
            val chipGroupLang = resources.getStringArray(R.array.chipProgramingLanguage)
            setChipGroup(chipGroupDev, chipGroupDevelopmentOccupationsEdit, categoryList)
            setChipGroup(chipGroupLang, chipProgramingLanguageEdit, categoryList)

            val limitPeopleText = data.userList.size.toString() + "/" + data.limitPerson
            tvLimitPeopleEdit.text = limitPeopleText
            tvLimitPeopleEdit.setOnClickListener {
                showDialogNumberPicker(tvLimitPeopleEdit, data.userList.size)
            }
            ivGroupImageEdit.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
            ivDeleteGroupImage.setOnClickListener {
                imageUri = null
                isGroupImageChange = true
                ivGroupImageEdit.setThumbnailByUrlOrDefault(null)
            }
            tvFirstDateEdit.setOnClickListener {
                showDialog(tvFirstDateEdit, Value.First)
            }
            tvLastDateEdit.setOnClickListener {
                showDialog(tvLastDateEdit, Value.Last)
            }
            btnCompleteEdit.setOnClickListener {
                btnCompleteEditOnClickListener(data)
            }
            btnDelete.setOnClickListener {
                try {
                    viewModel.deleteGroup(data.groupId, data.userList)
                    parentFragmentManager.popBackStack("Read", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                } catch (e : Exception) {
                    makeToastWithStringRes(requireContext(), R.string.error)
                }
            }
            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            etGroupNameEdit.setText(data.groupName)
            etGroupOneLineDescriptionEdit.setText(data.groupOneLineDescription)
            tvLeaderIdEdit.text = data.leaderId
            etGroupDescriptionEdit.setText(data.groupDescription)
            ivGroupImageEdit.setThumbnailByUrlOrDefault(data.groupThumbnail)
            tvLastDateEdit.text = data.lastDate
            tvFirstDateEdit.text = data.firstDate

            initSpinner(data.category.classification)

        }
    }

    private fun btnCompleteEditOnClickListener(data: GroupModel) {
        val categoryList = CategoryModel(
            categoryText,
            getChipText(binding.chipGroupDevelopmentOccupationsEdit),
            getChipText(binding.chipProgramingLanguageEdit)
        )
        val updatedGroupThumbnail = if (isGroupImageChange && imageUri == null) "" else data.groupThumbnail
        val str = binding.tvLimitPeopleEdit.text.toString().split("/")

        try {
            viewModel.updateGroup(
                data.copy(
                    groupName = binding.etGroupNameEdit.text.toString(),
                    groupOneLineDescription = binding.etGroupOneLineDescriptionEdit.text.toString(),
                    groupDescription = binding.etGroupDescriptionEdit.text.toString(),
                    firstDate = binding.tvFirstDateEdit.text.toString(),
                    lastDate = binding.tvLastDateEdit.text.toString(),
                    category = categoryList,
                    limitPerson = str[1],
                    groupThumbnail = updatedGroupThumbnail,
                ), imageUri
            )
            parentFragmentManager.popBackStack()
        } catch (e : Exception) {
            makeToastWithStringRes(requireContext(), R.string.error)
        }

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
                    p0?.let{
                        categoryText = it.getItemAtPosition(p2).toString()
                    }
/*                    if (p0 != null) {
                        categoryText = p0.getItemAtPosition(p2).toString()
                    }*/
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    return
                }
            }
    }
    private fun initTextWatcher(){
        with(binding){
            etGroupDescriptionEdit.addTextWatcherWithError(500, requireContext().getString(kr.nbc.momo.R.string.group_desc), btnCompleteEdit)
            etGroupOneLineDescriptionEdit.addTextWatcherWithError(30, requireContext().getString(kr.nbc.momo.R.string.group_one_line_desc), btnCompleteEdit)
            etGroupNameEdit.addTextWatcherWithError(30, requireContext().getString(kr.nbc.momo.R.string.group_name), btnCompleteEdit)
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

    private fun showDialogNumberPicker(textView: TextView, size: Int) {
        val dialogBinding = DialogSelectNumberBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.numberPicker.minValue = if (size < 5) 5 else size
        dialogBinding.numberPicker.maxValue = 100
        dialogBuilder.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnConfirm.setOnClickListener {
            dialogBuilder.dismiss()
            val limitPeopleText = size.toString() + "/" + dialogBinding.numberPicker.value
            textView.text = limitPeopleText
        }
        dialogBuilder.show()
    }
    private fun showDialog(groupId: String, userId: String, anDialog: EnumDialog) {
        val dialogBinding = DialogJoinProjectBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialogBuilder.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (anDialog == EnumDialog.LeaderChange) {
            dialogBinding.tvClose.text = getString(R.string.change_leader, userId)
            dialogBinding.btnConfirm.setOnClickListener {
                dialogBuilder.dismiss()
                try {
                    viewModel.leaderChange(groupId, userId)
                    parentFragmentManager.popBackStack()
                } catch (e : Exception) {
                    makeToastWithStringRes(requireContext(), R.string.error)
                }
            }
        }

        if (anDialog == EnumDialog.DeleteUser) {
            dialogBinding.tvClose.text = getString(R.string.ban_user, userId)
            dialogBinding.btnConfirm.setOnClickListener {
                dialogBuilder.dismiss()
                try {
                    viewModel.deleteUser(userId, groupId)
                } catch (e : Exception) {
                    makeToastWithStringRes(requireContext(), R.string.error)
                }
            }
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialogBuilder.dismiss()
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

            dateType.text = getString(R.string.yyyy_MM_dd, year, monthText, dayText)

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
        //val picker = DatePickerDialog(requireContext(), R.style.CustomDatePicker, listener, year, month, day)
        val picker = DatePickerDialog(requireContext(), listener, year, month, day)

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
}