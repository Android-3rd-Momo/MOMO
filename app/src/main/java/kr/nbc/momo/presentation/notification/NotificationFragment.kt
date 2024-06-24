package kr.nbc.momo.presentation.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentHomeBinding
import kr.nbc.momo.databinding.FragmentMyGroupBinding
import kr.nbc.momo.databinding.FragmentNotificationBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.home.HomeViewModel
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.makeToastWithStringRes
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToInvisible
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: String? = null
    private var userGroup: List<String> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()
        observeSubscriptionGroupList()
        initView()
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.currentUser.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        //No action needed
                    }

                    is UiState.Success -> {
                        if (uiState.data != null) {
                            Log.d("currentUser", uiState.data.userId)
                            currentUser = uiState.data.userId
                            userGroup = uiState.data.userGroup
                            initGroupList(uiState.data.userId)
                        }
                    }

                    is UiState.Error -> {

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
                        val notificationAdapter = NotificationAdapter(list)
                        binding.rvLeaderSub.adapter = notificationAdapter
                        binding.rvLeaderSub.layoutManager = LinearLayoutManager(requireContext())

                        notificationAdapter.confirm = object : NotificationAdapter.Confirm {
                            override fun confirm(groupId: String, userId: String) {
                                lifecycleScope.launch {
                                    try {
                                        viewModel.addUser(userId, groupId)
                                        currentUser?.let { initGroupList(it) }
                                    } catch (e : Exception) {
                                        makeToastWithStringRes(requireContext(), R.string.error)
                                    }
                                }
                            }
                        }

                        notificationAdapter.reject = object : NotificationAdapter.Reject {
                            override fun reject(groupId: String, userId: String) {
                                lifecycleScope.launch {
                                    try {
                                        viewModel.rejectUser(userId, groupId)
                                        currentUser?.let { initGroupList(it) }
                                    } catch (e : Exception) {
                                        makeToastWithStringRes(requireContext(), R.string.error)
                                    }
                                }
                            }
                        }

                        notificationAdapter.userClick = object : NotificationAdapter.UserClick {
                            override fun userClick(userId: String) {
                                sharedViewModel.getUserId(userId)
                                (activity as? MainActivity)?.beginTransactionUserInfo()
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

    private fun initView() {
        binding.ivReturn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun initGroupList(userId: String) {
        lifecycleScope.launch {
            viewModel.getSubscriptionList(userId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}