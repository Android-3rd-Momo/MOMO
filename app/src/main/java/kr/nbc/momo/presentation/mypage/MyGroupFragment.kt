package kr.nbc.momo.presentation.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.databinding.FragmentMyGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.SharedViewModel

@AndroidEntryPoint
class MyGroupFragment : Fragment() {
    private var _binding: FragmentMyGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()
        observeSubscriptionGroupList()
        observeAddUser()
    }

    private fun initGroupList(userId: String) {
        lifecycleScope.launch {
            viewModel.getSubscriptionList(userId)
        }
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
                            if (uiState.data != null) {
                                Log.d("currentUser", uiState.data.userId)
                                currentUser = uiState.data.userId
                                initGroupList(uiState.data.userId)
                            }
                        }

                        is UiState.Error -> {

                        }
                    }
                }
            }
        }
    }

    private fun observeAddUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.adduserState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 오류 메시지 표시

                    }
                    is UiState.Success -> {
                        currentUser?.let { initGroupList(it) }
                    }

                    is UiState.Error -> {
                        // 오류 메시지 표시
                        Log.d("error", uiState.message)
                    }
                }
            }
        }

    }

    private fun observeSubscriptionGroupList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.subscriptionListState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // 오류 메시지 표시

                    }

                    is UiState.Success -> {
                        Log.d("uiState", "${uiState.data}")
                        val list = listOf<Pair<GroupModel, String>>().toMutableList()
                        for (i in uiState.data) {
                            if (i.subscriptionList.isNotEmpty()) {
                                for (j in i.subscriptionList) {
                                    list.add(Pair(i, j))
                                }
                            }
                        }
                        val adapter = SubscriptionGroupAdapter(list)
                        binding.rvSubscriptionGroupList.adapter = adapter
                        binding.rvSubscriptionGroupList.layoutManager = LinearLayoutManager(requireContext())

                        adapter.itemClick = object : SubscriptionGroupAdapter.ItemClick {
                            override fun itemClick(groupId: String, userId: String) {
                                viewModel.addUser(userId, groupId)
                                currentUser?.let { initGroupList(it) }
                            }
                        }

                    }

                    is UiState.Error -> {
                        // 오류 메시지 표시
                        Log.d("error", uiState.message)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}