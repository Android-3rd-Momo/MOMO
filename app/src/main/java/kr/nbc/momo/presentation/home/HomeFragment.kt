package kr.nbc.momo.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentHomeBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.create.CreateGroupFragment

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGroupList()
        initView()
    }

    private fun initView() {
        binding.floatingBtnCreateGroup.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCreateGroupFragment2()
            findNavController().navigate(action)

        }
    }

    private fun initGroupList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getGroupList.collect { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            Log.d("UiState", uiState.message)
                        }
                        UiState.Loading -> {
                            // TODO()
                        }
                        is UiState.Success -> {
                            homeAdapter = HomeAdapter(uiState.data)
                            binding.rvGroupList.adapter = homeAdapter
                            binding.rvGroupList.layoutManager =
                                LinearLayoutManager(requireContext())
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