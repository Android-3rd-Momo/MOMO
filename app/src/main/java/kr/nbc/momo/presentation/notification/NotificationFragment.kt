package kr.nbc.momo.presentation.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentNotificationBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.hideNav
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.makeToastWithStringRes
import kr.nbc.momo.util.showNav

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
        hideNav()
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

                            override fun confirm(
                                groupId: String,
                                userId: String,
                                limitPerson: Int,
                                userListSize: Int
                            ) {
                                lifecycleScope.launch {
                                    try {
                                        if (userListSize < limitPerson) {
                                            viewModel.addUser(userId, groupId)
                                            currentUser?.let { initGroupList(it) }
                                        } else {
                                            makeToastWithStringRes(requireContext(), R.string.exceeded_Number)
                                        }
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
                                findNavController().navigate(R.id.action_notificationFragment_to_userInfoFragment)
                                //(activity as? MainActivity)?.beginTransactionUserInfo()
                            }
                        }

                    }

                    is UiState.Error -> {
                        // 오류 메시지 표시
                        Log.d("error", uiState.message)
                        makeToastWithString(requireContext(), uiState.message)
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.ivReturn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initGroupList(userId: String) {
        lifecycleScope.launch {
            viewModel.getSubscriptionList(userId)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        showNav()
        _binding = null
    }

}