package kr.nbc.momo.presentation.group.read

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class ReadGroupFragment : Fragment() {
    private var _binding: FragmentReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
        initGroup()
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

    private fun initGroup() {
        lifecycleScope.launch {
            sharedViewModel.groupName.observe(viewLifecycleOwner) {
                viewModel.readGroup(it)
            }
        }

        lifecycleScope.launch {
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
                            ivGroupImage.load(uiState.data.groupThumbnail)
                            tvGroupName.text = uiState.data.groupName
                            tvGroupOneLineDescription.text = uiState.data.groupOneLineDescription
                            tvGroupDescription.text = uiState.data.groupDescription
                            tvFirstDate.text = uiState.data.firstDate
                            tvLastDate.text = uiState.data.lastDate
                            tvLeaderId.text = uiState.data.leaderId

                        }

                        if (uiState.data.userList.contains("userId").not()) {
                            binding.btnJoinProject.setVisibleToVisible()
                        }
                    }
                }
            }
        }
    }
}

