package kr.nbc.momo.presentation.group.read

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentCreateGroupBinding
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.create.CreateGroupViewModel

@AndroidEntryPoint
class ReadGroupFragment : Fragment() {
    private var _binding: FragmentReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadGroupViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGroup()
    }

    private fun initGroup() {
        lifecycleScope.launch {
            viewModel.readGroup("Nqcpz10GiEAK10i4qPql")

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.readGroup.collect { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            Log.d("UiState", uiState.message)
                        }
                        UiState.Loading -> {
                            // TODO()
                        }
                        is UiState.Success -> {
                            with(binding) {
                                groupName.text = uiState.data.groupName
                                groupOneLineDescription.text = uiState.data.groupOneLineDescription
                                groupDescription.text = uiState.data.groupDescription
                                firstDate.text = uiState.data.firstDate
                                lastDate.text = uiState.data.lastDate
                                leaderId.text = uiState.data.leaderId
                            }
                        }
                    }
                }
            }
        }
    }




    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
