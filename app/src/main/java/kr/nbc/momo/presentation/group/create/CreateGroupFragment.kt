package kr.nbc.momo.presentation.group.create

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentCreateGroupBinding
import kr.nbc.momo.presentation.group.model.GroupModel
import java.util.Calendar

@AndroidEntryPoint
class CreateGroupFragment : Fragment() {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateGroupViewModel by viewModels()
    private var imageUri: Uri? = null
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivGroupImage.setImageURI(uri)
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
        binding.button.setOnClickListener {
            createGroup()
        }
        initView()
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

    private fun initView() {
        binding.firstDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            // Calendar 에서 월, 일, 시간을 받아옴

            val listener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                val mon = month + 1
                val monthText = if (mon < 10) {
                    "0$mon"
                } else mon.toString()

                val dayText = if (day < 10) {
                    "0$day"
                } else day.toString()
                binding.firstDate.text = "$year.$monthText.$dayText"
            }

            var picker = DatePickerDialog(requireContext(), listener, year, month, day)
            picker.show()
        }

        binding.lastDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            // Calendar 에서 월, 일, 시간을 받아옴

            val listener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                val mon = month + 1
                val monthText = if (mon < 10) {
                    "0$mon"
                } else mon.toString()

                val dayText = if (day < 10) {
                    "0$day"
                } else day.toString()
                binding.lastDate.text = "$year.$monthText.$dayText"
            }

            var picker = DatePickerDialog(requireContext(), listener, year, month, day)
            picker.show()
        }

        binding.tvCategory.setOnClickListener {
            if (binding.clCheckBoxContainer.visibility == View.GONE) binding.clCheckBoxContainer.visibility = View.VISIBLE
            else binding.clCheckBoxContainer.visibility = View.GONE
        }

        binding.ivGroupImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
    }

    private fun createGroup() {
        val categoryList = listOf(binding.check1, binding.check2, binding.check3)
            .filter { it.isChecked }
            .map { it.text.toString() }

        val group = GroupModel(
            binding.groupName.text.toString(),
            binding.groupOneLineDescription.text.toString(),
            imageUri.toString(),
            binding.groupDescription.text.toString(),
            binding.firstDate.text.toString(),
            binding.lastDate.text.toString(),
            "",
            categoryList,
            listOf("")
        )

        viewModel.createGroup(group) { success, exception ->
            if (success) {
                Log.d("CreateGroup", "success")

            } else {
                Log.d("CreateGroupException", exception.toString())
            }
        }

    }

}
