package kr.nbc.momo.presentation.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import kr.nbc.momo.databinding.FragmentMyGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.mypage.adapter.LeaderGroupAdapter
import kr.nbc.momo.presentation.mypage.adapter.LeaderSubAdapter
import kr.nbc.momo.presentation.mypage.adapter.MemberGroupAdapter
import kr.nbc.momo.presentation.mypage.adapter.MemberSubAdapter
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToInvisible
import kr.nbc.momo.util.setVisibleToVisible
import kr.nbc.momo.util.showNav

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
        observeAddUser()
        observerUserGroup()
        observerUserAppliedGroup()
        observeRejectUser()
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
                                userGroup = uiState.data.userGroup
                                initGroupList(uiState.data.userId, uiState.data.userGroup)
                            } else {
                                with(binding) {
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
                        currentUser?.let { initGroupList(it, userGroup) }
                    }

                    is UiState.Error -> {
                        // 오류 메시지 표시
                        Log.d("error", uiState.message)
                    }
                }
            }
        }

    }

    private fun observeRejectUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.rejectUserState.collect() { uiState ->
                when (uiState) {
                    is UiState.Loading -> {

                    }
                    is UiState.Success -> {
                        Log.d("Success","Success")
                        currentUser?.let { initGroupList(it, userGroup) }
                    }

                    is UiState.Error -> {
                        Log.d("error", uiState.message)
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
                        with(binding) {
                            prCircularMember.setVisibleToVisible()
                            includeNoResultMember.setVisibleToGone()
                            rvMember.setVisibleToInvisible()
                        }

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
                        // 오류 메시지 표시

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
                        with(binding) {
                            prCircularLeader.setVisibleToVisible()
                            includeNoResultLeader.setVisibleToGone()
                            rvLeader.setVisibleToInvisible()
                        }

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
                            override fun confirm(groupId: String, userId: String) {
                                viewModel.addUser(userId, groupId)
                                currentUser?.let { initGroupList(it, userGroup) }
                            }
                        }

                        leaderSubAdapter.reject = object : LeaderSubAdapter.Reject {
                            override fun reject(groupId: String, userId: String) {
                                viewModel.rejectUser(userId, groupId)
                                currentUser?.let { initGroupList(it, userGroup) }
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