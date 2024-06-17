package kr.nbc.momo.presentation.group.create

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.DialogJoinProjectBinding
import kr.nbc.momo.databinding.DialogSelectNumberBinding
import kr.nbc.momo.databinding.FragmentCreateGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.CategoryModel
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.group.read.Dialog
import kr.nbc.momo.presentation.group.read.ReadGroupFragment
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.encryptECB
import java.util.Calendar
import kotlin.math.max

@AndroidEntryPoint
class CreateGroupFragment : Fragment() {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateGroupViewModel by viewModels()
    private var imageUri: Uri? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var categoryText: String = ""
    private lateinit var currentUser: String
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavHide()
        observeUserProfile()
        observeCreateGroup()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        bottomNavShow()
    }

    private fun bottomNavHide() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.visibility = View.GONE
    }

    private fun bottomNavShow() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.visibility = View.VISIBLE
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //fragment의 수명주기가 해당 상태일 때만 실행되도록 보장
                sharedViewModel.currentUser.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            //No action needed
                        }

                        is UiState.Success -> {
                            if (state.data != null) {
                                Log.d("currentUser", state.data.userId)
                                currentUser = state.data.userId
                                initView()
                            } else {
                                parentFragmentManager.popBackStack()
                                Toast.makeText(requireContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }


                        is UiState.Error -> {
                            Log.d("error", state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeCreateGroup() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createGroupState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 로딩 처리 (필요한 경우)
                    }

                    is UiState.Success -> {
                        Toast.makeText(requireContext(), "그룹 생성 성공", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                        val readGroupFragment = ReadGroupFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, readGroupFragment)
                            .addToBackStack(null)
                            .commit()
                    }

                    is UiState.Error -> {
                        Log.d("error", uiState.message)
                    }
                }

            }
        }
    }

    private fun initView() {
        with(binding) {
            groupDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //No action needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val textLength = binding.groupDescription.text.length
                    if (textLength > 500) {
                        binding.groupDescription.error = "그룹 소개는 500자까지 작성 가능합니다."
                        btnCreateProject.isEnabled = false
                    } else {
                        binding.groupDescription.error = null
                        btnCreateProject.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //No action needed
                }

            })

            groupOneLineDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //No action needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val textLength = binding.groupOneLineDescription.text.length
                    if (textLength > 30) {
                        binding.groupOneLineDescription.error = "그룹 한 줄 소개는 30자까지 작성 가능합니다."
                        btnCreateProject.isEnabled = false
                    } else {
                        binding.groupOneLineDescription.error = null
                        btnCreateProject.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //No action needed
                }

            })

            groupName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //No action needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val textLength = binding.groupName.text.length
                    if (textLength > 30) {
                        binding.groupName.error = "그룹 이름은 30자까지 작성 가능합니다."
                        btnCreateProject.isEnabled = false
                    } else {
                        binding.groupName.error = null
                        btnCreateProject.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //No action needed
                }

            })



            categorySpinner.clipToOutline = true
            ivGroupImage.clipToOutline = true

            val chipGroupDev = resources.getStringArray(R.array.chipGroupDevelopmentOccupations)
            val chipGroupLang = resources.getStringArray(R.array.chipProgramingLanguage)
            setChipGroup(chipGroupDev, chipGroupDevelopmentOccupations)
            setChipGroup(chipGroupLang, chipProgramingLanguage)

            tvLimitPeople.setOnClickListener {
                showDialogNumberPicker(tvLimitPeople)
            }

            firstDate.setOnClickListener {
                showDialog(firstDate)
            }

            lastDate.setOnClickListener {
                showDialog(lastDate)
            }

            clCategoryDetail.setOnClickListener {
                val viewArr = listOf(
                    chipGroupDevelopmentOccupations,
                    chipProgramingLanguage,
                    tvDevelopmentOccupations,
                    tvProgramingLanguage
                )
                viewArr.forEach {
                    if (it.visibility == View.GONE) {
                        it.visibility = View.VISIBLE
                    } else {
                        it.visibility = View.GONE
                    }
                }
            }

            ivGroupImage.clipToOutline = true
            ivGroupImage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }

            btnCreateProject.setOnClickListener {
                if (firstDate.text.isEmpty() || lastDate.text.isEmpty() || groupName.text.isEmpty() ||
                    groupDescription.text.isEmpty() || groupOneLineDescription.text.isEmpty()
                ) {
                    Toast.makeText(requireContext(), "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show()
                } else if (categoryText == "카테고리" ||
                    binding.chipProgramingLanguage.checkedChipIds.size +
                    binding.chipGroupDevelopmentOccupations.checkedChipIds.size < 1
                ) {
                    Toast.makeText(requireContext(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    showDialog()
                }
            }

            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
        initSpinner()
        binding.clHome.setOnClickListener {
            hideKeyboard(requireActivity() as Activity)
        }

    }

    private fun initSpinner() {
        val items = resources.getStringArray(R.array.classification)
        val spinnerAapter =
            object : ArrayAdapter<String>(requireContext(), R.layout.spinner_item_category) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)

                    if (position == count) {
                        val textView = (v.findViewById<View>(R.id.tvCategorySpinner) as TextView)
                        textView.text = ""
                        textView.hint = getItem(count)
                    }

                    return v
                }

                override fun getCount(): Int {
                    return super.getCount() - 1
                }
            }

        spinnerAapter.addAll(items.toMutableList())
        spinnerAapter.add("카테고리")
        binding.categorySpinner.adapter = spinnerAapter
        binding.categorySpinner.setSelection(spinnerAapter.count)
        binding.categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p0 != null) {
                        categoryText = p0.getItemAtPosition(p2).toString()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    categoryText = ""
                    return
                }
            }
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

    private fun createGroup() {
        val categoryList = CategoryModel(
            categoryText,
            getChipText(binding.chipGroupDevelopmentOccupations),
            getChipText(binding.chipProgramingLanguage)
        )


        val image = if (imageUri != null) imageUri.toString() else null
        val groupId = binding.groupName.text.toString().encryptECB()
        val group = GroupModel(
            groupId,
            binding.groupName.text.toString(),
            binding.groupOneLineDescription.text.toString(),
            image,
            binding.groupDescription.text.toString(),
            binding.firstDate.text.toString(),
            binding.lastDate.text.toString(),
            currentUser,
            categoryList,
            listOf(currentUser),
            binding.tvLimitPeople.text.toString()
        )

        lifecycleScope.launch {
            viewModel.createGroup(group)
            sharedViewModel.getGroupId(groupId)
            viewModel.joinGroup(groupId)
        }
    }

    private fun showDialog() {
        val dialogBinding = DialogJoinProjectBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvClose.text = "프로젝트를 생성합니다"
        dialogBinding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            createGroup()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogNumberPicker(textView: TextView) {
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
            textView.text = dialogBinding.numberPicker.value.toString()

        }
        dialogBuilder.show()
    }


    private fun setChipGroup(chipList: Array<String>, chipGroup: ChipGroup) {
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

                // setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.tv_chip_state_color))
                // setChipDrawable(ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.Widget_Chip))
            }
            chipGroup.addView(chip)
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

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.applicationWindowToken, 0)
    }
}