package kr.nbc.momo.presentation.group.read

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.DialogJoinProjectBinding
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattingroom.ChattingRoomFragment
import kr.nbc.momo.presentation.group.model.CategoryModel
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.signup.SignUpFragment
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible
import java.util.Calendar

@AndroidEntryPoint
class ReadGroupFragment : Fragment() {
    private var _binding: FragmentReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser : String? = null
    private var isEditMode = false
    private var imageUri: Uri? = null
    private var image: String? = null
    private var categoryText : String = "공모전"
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivGroupImage.load(uri)
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
        bottomNavHide()
        observeGroupState()
        observeUserProfile()
        observeUpdateState()
        observeUserList()
        observeDeleteGroup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
                            // todo 로딩
                        }

                        is UiState.Success -> {
                            Log.d("currentUser", uiState.data.userId)
                            currentUser = uiState.data.userId
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
                        // 로딩 처리 (필요한 경우)
                    }

                    is UiState.Success -> {
                        initView(uiState.data)
                        initGroupThumbnail(uiState.data.groupThumbnail)
                    }

                    is UiState.Error -> {
                        // 오류 메시지 표시
                        Log.d("error", uiState.message)
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
                        // 로딩 처리 (필요한 경우)
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
                        if (uiState.data) {
                            parentFragmentManager.popBackStack()
                        } else {
                            // 그룹 삭제 실패
                        }
                    }

                    is UiState.Error -> {
                        // 이외 다른 예외
                        Log.d("error", uiState.message)
                    }
                }

            }
        }
    }


    private fun initGroup() {
        lifecycleScope.launch {
            sharedViewModel.groupId.observe(viewLifecycleOwner) {
                if (it != null) {
                    viewModel.readGroup(it)
                }
            }
        }
    }

    private fun initView(data: GroupModel) {
        with(binding) {
            tvCategoryClassification.text = data.category.classification
            tvGroupName.text = data.groupName
            tvGroupOneLineDescription.text = data.groupOneLineDescription
            tvGroupDescription.text = data.groupDescription
            tvFirstDate.text = data.firstDate
            tvLastDate.text = data.lastDate
            tvLeaderId.text = data.leaderId
            tvDetailCategoryList.text = data.category.developmentOccupations.joinToString().plus(", " + data.category.programingLanguage.joinToString())

            if (data.userList.contains(currentUser)) binding.btnJoinProject.text = "채팅방 이동"
            if (data.leaderId == currentUser) binding.btnEdit.visibility = View.VISIBLE

            initUserList(data.userList)
            btnJoinProjectClickListener(currentUser, data)
            btnEditClickListener(data)
        }
        initSpinner()
    }

    private fun initGroupThumbnail(groupThumbnail: String?) {
        binding.ivGroupImage.load(groupThumbnail)
    }

    private fun initUserList(userList: List<String>) {
        val adapter = UserListAdapter(userList)
        binding.rvUserList.adapter = adapter
        binding.rvUserList.layoutManager = GridLayoutManager(requireContext(), 5)
    }

    private fun btnJoinProjectClickListener(currentUser: String?, data: GroupModel) {
        if (currentUser == null) {
            binding.btnJoinProject.setOnClickListener {
                showDialog(false, data, currentUser)
            }
        } else {
            if (data.userList.contains(currentUser)) {
                binding.btnJoinProject.setOnClickListener {
                    val chattingRoomFragment = ChattingRoomFragment()
                    parentFragmentManager.popBackStack()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, chattingRoomFragment)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                binding.btnJoinProject.setOnClickListener {
                    showDialog(true, data, currentUser)
                }
            }
        }
    }

    private fun btnEditClickListener(data: GroupModel) {
        binding.btnEdit.setOnClickListener {
            setEditMode(data)
        }
    }

    private fun setEditMode(data: GroupModel) {
        binding.etGroupName.setText(data.groupName)
        binding.etGroupDescription.setText(data.groupDescription)
        binding.etGroupOneLineDescription.setText(data.groupOneLineDescription)

        val categoryList = data.category.programingLanguage + data.category.developmentOccupations

        val chips = listOf(binding.chipFront, binding.chipBack, binding.chipServer, binding.chipGame, binding.chipAI,
            binding.chipAOS, binding.chipIOS, binding.chipUIUX, binding.chipEtc, binding.chipFrontLang,
            binding.chipJava, binding.chipPython, binding.chipCSharp, binding.chipCpp, binding.chipKotlin
        )

        chips.forEach { chip ->
            chip.isChecked = categoryList.contains(chip.text)
        }

        val editMode = arrayOf(
            binding.etGroupName,
            binding.etGroupDescription,
            binding.etGroupOneLineDescription,
            binding.tvCategory,
            binding.clCategoryDetail,
            binding.categorySpinner,
            binding.btnCompleteEdit,
            binding.btnDelete,
        )
        val viewMode = arrayOf(
            binding.tvCategoryClassification,
            binding.tvGroupName,
            binding.tvGroupDescription,
            binding.tvGroupOneLineDescription,
            binding.btnJoinProject,
            binding.btnEdit,
            binding.tvDetailCategoryList,
            binding.tvDetailCategory,
            binding.rvUserList
        )

        binding.ivGroupImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        binding.tvFirstDate.setOnClickListener {
            showDialog(binding.tvFirstDate)
        }
        binding.tvLastDate.setOnClickListener {
            showDialog(binding.tvLastDate)
        }
        binding.btnCompleteEdit.setOnClickListener {
            btnCompleteEditOnClickListener(data, editMode, viewMode)
        }
        binding.btnDelete.setOnClickListener {
            viewModel.deleteGroup(data.groupId)
        }
        binding.btnDelete.setOnClickListener {
            viewModel.deleteGroup(data.groupId)
        }

        setChangeMode(editMode, viewMode)
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.classification,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        binding.categorySpinner.onItemSelectedListener =
            object: AdapterView.OnItemSelectedListener {
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
            listOf(binding.chipBack, binding.chipFront, binding.chipServer, binding.chipGame,
                binding.chipAI, binding.chipIOS, binding.chipWeb, binding.chipUIUX, binding.chipEtc)
                .filter { it.isChecked }
                .map { it.text.toString() },
            listOf(binding.chipFrontLang, binding.chipJava, binding.chipPython, binding.chipCSharp, binding.chipCpp)
                .filter { it.isChecked }
                .map { it.text.toString() }
        )


        image = data.groupThumbnail
        viewModel.updateGroup(
            data.copy(
                groupName = binding.etGroupName.text.toString(),
                groupOneLineDescription = binding.etGroupOneLineDescription.text.toString(),
                groupDescription = binding.etGroupDescription.text.toString(),
                firstDate = binding.tvFirstDate.text.toString(),
                lastDate = binding.tvLastDate.text.toString(),
                category = categoryList
            ), imageUri
        )
        setChangeMode(editMode, viewMode)
    }

    private fun showDialog(dateType: TextView) {
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
        }

        var picker = DatePickerDialog(requireContext(), listener, year, month, day)
        picker.show()
    }


    private fun showDialog(loginBoolean: Boolean, data: GroupModel, currentUser: String?) {
        val dialogBinding = DialogJoinProjectBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        if (loginBoolean) {
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                lifecycleScope.launch {
                    val list = data.userList.toMutableList()
                    list.add(currentUser!!)
                    viewModel.addUser(list, data.groupId)
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
}