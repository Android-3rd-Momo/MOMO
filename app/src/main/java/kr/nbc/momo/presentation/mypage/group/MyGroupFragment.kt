package kr.nbc.momo.presentation.mypage.group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentMyGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.mypage.group.adapter.LeaderGroupAdapter
import kr.nbc.momo.presentation.mypage.group.adapter.LeaderSubAdapter
import kr.nbc.momo.presentation.mypage.group.adapter.MemberGroupAdapter
import kr.nbc.momo.presentation.mypage.group.adapter.MemberSubAdapter
import kr.nbc.momo.util.makeToastWithStringRes
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToInvisible
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class MyGroupFragment : Fragment() {
    private var _binding: FragmentMyGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser: String? = null
    private var userGroup: List<String> = listOf()
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
        observerUserGroup()
        observerUserAppliedGroup()
        initView()
    }

    private fun initGroupList(userId: String, groupList: List<String>) {
        lifecycleScope.launch {
            viewModel.getSubscriptionList(userId)
            viewModel.getAppliedGroupList(userId)
            viewModel.getUserGroup(groupList, userId)
        }
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.currentUser.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        //No action needed
                        Log.d("UserGroups", "loading")
                        with(binding) {
                            prCircularLeader.setVisibleToVisible()
                            includeNoResultLeader.setVisibleToGone()
                            rvLeader.setVisibleToInvisible()
                            prCircularMember.setVisibleToVisible()
                            includeNoResultMember.setVisibleToGone()
                            rvMember.setVisibleToInvisible()
                        }
                    }

                    is UiState.Success -> {
                        if (uiState.data != null) {
                            Log.d("UserGroups", "success not null")
                            currentUser = uiState.data.userId
                            userGroup = uiState.data.userGroup
                            initGroupList(uiState.data.userId, uiState.data.userGroup)
                        } else {
                            with(binding) {
                                Log.d("UserGroups", "success null")
                                prCircularLeader.setVisibleToGone()
                                includeNoResultLeader.setVisibleToVisible()
                                rvLeader.setVisibleToInvisible()
                                prCircularMember.setVisibleToGone()
                                includeNoResultMember.setVisibleToVisible()
                                rvMember.setVisibleToInvisible()
                            }
                        }
                    }

                    is UiState.Error -> {
                        Log.d("UserGroups", "error")
                    }
                }

            }
        }
    }


    private fun observerUserGroup() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userGroupList.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
/*
                        with(binding) {
                            prCircularMember.setVisibleToVisible()
                            includeNoResultMember.setVisibleToGone()
                            rvMember.setVisibleToInvisible()
                        }
*/

                    }

                    is UiState.Success -> {
                        val filterData = uiState.data.filterNot { it.leaderId == currentUser }

                        val memberGroupAdapter = MemberGroupAdapter(filterData)
                        binding.rvMember.adapter = memberGroupAdapter
                        binding.rvMember.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                        if (filterData.isEmpty()) {
                            binding.prCircularMember.setVisibleToGone()
                            binding.includeNoResultMember.setVisibleToVisible()
                            binding.rvMember.setVisibleToInvisible()
                        } else {
                            binding.prCircularMember.setVisibleToGone()
                            binding.includeNoResultMember.setVisibleToGone()
                            binding.rvMember.setVisibleToVisible()
                        }

                        memberGroupAdapter.itemClick = object : MemberGroupAdapter.ItemClick {
                            override fun itemClick(position: Int) {
                                val groupId = filterData[position].groupId
                                sharedViewModel.getGroupId(groupId)
                                (activity as? MainActivity)?.beginTransactionRead()
                            }
                        }

                    }

                    is UiState.Error -> {
                        Log.d("userGroupList",uiState.message)
                        binding.prCircularMember.setVisibleToGone()
                        binding.includeNoResultMember.setVisibleToVisible()
                        binding.rvMember.setVisibleToInvisible()
                    }
                }
            }
        }
    }


    private fun observerUserAppliedGroup() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userAppliedGroupList.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        val memberSubAdapter = MemberSubAdapter(uiState.data)
                        binding.rvMemberSub.adapter = memberSubAdapter
                        binding.rvMemberSub.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                        memberSubAdapter.itemClick = object : MemberSubAdapter.ItemClick {
                            override fun itemClick(position: Int) {
                                val groupId = uiState.data[position].groupId
                                sharedViewModel.getGroupId(groupId)
                                (activity as? MainActivity)?.beginTransactionRead()
                            }
                        }

                        memberSubAdapter.exitClick = object : MemberSubAdapter.ExitClick {
                            override fun exitClick(groupId: String) {
                                lifecycleScope.launch {
                                    try {
                                        currentUser?.let { viewModel.rejectUser(it, groupId) }
                                        currentUser?.let { initGroupList(it, userGroup) }
                                    } catch (e : Exception) {
                                        makeToastWithStringRes(requireContext(), R.string.error)
                                    }
                                }
                            }
                        }

                    }

                    is UiState.Error -> {
                        Log.d("userAppliedGroupList",uiState.message)
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
/*                        with(binding) {
                            prCircularLeader.setVisibleToVisible()
                            includeNoResultLeader.setVisibleToGone()
                            rvLeader.setVisibleToInvisible()
                        }*/

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
                        val leaderSubAdapter = LeaderSubAdapter(list)
                        binding.rvLeaderSub.adapter = leaderSubAdapter
                        binding.rvLeaderSub.layoutManager = LinearLayoutManager(requireContext())

                        leaderSubAdapter.confirm = object : LeaderSubAdapter.Confirm {
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
                                            currentUser?.let { initGroupList(it, userGroup) }
                                        } else {
                                            makeToastWithStringRes(requireContext(), R.string.exceeded_Number)
                                        }
                                    } catch (e : Exception) {
                                        makeToastWithStringRes(requireContext(), R.string.error)
                                    }
                                }
                            }
                        }

                        leaderSubAdapter.reject = object : LeaderSubAdapter.Reject {
                            override fun reject(groupId: String, userId: String) {
                                lifecycleScope.launch {
                                    try {
                                        viewModel.rejectUser(userId, groupId)
                                        currentUser?.let { initGroupList(it, userGroup) }
                                    } catch (e : Exception) {
                                        makeToastWithStringRes(requireContext(), R.string.error)
                                    }
                                }
                            }
                        }

                        leaderSubAdapter.userClick = object : LeaderSubAdapter.UserClick {
                            override fun userClick(userId: String) {
                                sharedViewModel.getUserId(userId)
                                (activity as? MainActivity)?.beginTransactionUserInfo()
                            }
                        }


                        val leaderGroupAdapter = LeaderGroupAdapter(uiState.data)
                        binding.rvLeader.adapter = leaderGroupAdapter
                        binding.rvLeader.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                        leaderGroupAdapter.itemClick = object : LeaderGroupAdapter.ItemClick {
                            override fun itemClick(position: Int) {
                                val groupId = uiState.data[position].groupId
                                sharedViewModel.getGroupId(groupId)
                                (activity as? MainActivity)?.beginTransactionRead()
                            }
                        }

                        if (uiState.data.isEmpty()) {
                            binding.prCircularLeader.setVisibleToGone()
                            binding.includeNoResultLeader.setVisibleToVisible()
                            binding.rvLeader.setVisibleToInvisible()
                        } else {
                            binding.prCircularLeader.setVisibleToGone()
                            binding.includeNoResultLeader.setVisibleToGone()
                            binding.rvLeader.setVisibleToVisible()
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
        with(binding) {
            includeNoResultMember.tvNoResult.setText(R.string.memberGroupNoResult)
            includeNoResultLeader.tvNoResult.setText(R.string.leaderGroupNoResult)

            tvMemberSub.setOnClickListener {
                rvLeaderSub.setVisibleToGone()
                rvMemberSub.setVisibleToVisible()
                tvLeaderSub.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black))
                tvMemberSub.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.blue))
            }

            tvLeaderSub.setOnClickListener {
                rvMemberSub.setVisibleToGone()
                rvLeaderSub.setVisibleToVisible()
                tvMemberSub.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black))
                tvLeaderSub.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.blue))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}