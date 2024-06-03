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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.presentation.UiState

private const val ARG_GROUP = "group"
@AndroidEntryPoint
class ReadGroupFragment : Fragment() {
    private var groupName: String? = null
    private var _binding: FragmentReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadGroupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // let을 통한 null값 처리
            groupName = it.getString(ARG_GROUP)
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
            groupName?.let { viewModel.readGroup(it) }

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

                            if (uiState.data.userList.contains("userId").not()) {
                                binding.button.visibility = View.VISIBLE
                            }

                        }
                    }
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(groupName: String) =
            ReadGroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_GROUP, groupName)
                }
            }
    }
}
