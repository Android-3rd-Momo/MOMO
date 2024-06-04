package kr.nbc.momo.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentHomeBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.create.CreateGroupFragment
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.group.read.ReadGroupFragment
import kr.nbc.momo.presentation.main.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
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
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateGroupFragment())
                .addToBackStack(null)
                .commit()

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
                            homeAdapter = HomeAdapter(
                                uiState.data.filter { it.lastDate >= getCurrentTime() && it.firstDate <= getCurrentTime()  }
                            )
                            binding.rvGroupList.adapter = homeAdapter
                            binding.rvGroupList.layoutManager = LinearLayoutManager(requireContext())
                            onClick(uiState.data)
                        }
                    }
                }
            }
        }
    }

    private fun onClick(data: List<GroupModel>) {
        homeAdapter.itemClick = object : HomeAdapter.ItemClick{
            override fun itemClick(position: Int) {
                val groupName = data[position].groupName
                sharedViewModel.getGroupName(groupName)
                val readGroupFragment = ReadGroupFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, readGroupFragment)
                    .addToBackStack(null)
                    .commit()
            }

        }
    }

    fun getCurrentTime() : String {
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(Date().time)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}