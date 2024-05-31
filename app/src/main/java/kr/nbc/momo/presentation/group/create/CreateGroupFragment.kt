package kr.nbc.momo.presentation.group.create

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentCreateGroupBinding
import kr.nbc.momo.presentation.group.model.GroupModel

@AndroidEntryPoint
class CreateGroupFragment : Fragment() {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.button.setOnClickListener {
            createGroup()
        }
    }

    private fun createGroup() {
        val group = GroupModel(
            binding.groupName.text.toString(),
            binding.groupOneLineDescription.text.toString(),
            binding.ivGroupImage.toString(),
            binding.groupDescription.text.toString(),
            binding.firstDate.text.toString(),
            binding.lastDate.text.toString(),
            binding.leaderId.text.toString(),
            listOf(""),
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


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}