package kr.nbc.momo.presentation.chatting.chattingroom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentChattingRoomBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.userinfo.UserInfoFragment
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class ChattingRoomFragment : Fragment() {
    private var _binding: FragmentChattingRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChattingRoomViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var chattingListModel = ChattingListModel()

    //공유 뷰모델에서 로그인 정보 받아오기
    private var currentUserId = ""
    private var currentUsername = ""
    private var currentUrl = ""
    private val rvAdapter = ChattingRecyclerViewAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingRoomBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGroupId()
        observeGroupListItem()
        hideNav()
        initData()
        observeChatList()
        initView()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.setLastViewedChat(chattingListModel.groupId, currentUserId, currentUsername)
    }

    override fun onDestroyView() {
        showNav()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeGroupId() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.groupId.collectLatest { groupId ->
                groupId?.let {
                    viewModel.getChatListItemById(it)
                }
            }
        }
    }

    private fun observeGroupListItem() {
        lifecycleScope.launch {
            viewModel.chatListItem.collectLatest {
                when (it) {
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        chattingListModel = it.data
                        binding.tvTitle.text = it.data.groupName
                        viewModel.getChatMessages(it.data.groupId)
                        Log.d("ChattingRoom", "${it.data}")
                    }

                    is UiState.Error -> {
                        Log.d("ChattingRoom", "${it.message}")
                    }
                }
            }
        }
    }

    private fun observeChatList() {
        lifecycleScope.launch {
            viewModel.chatMessages.collectLatest { chatMessages ->
                when (chatMessages) {
                    is UiState.Loading -> {
                        binding.rvChatMessage.setVisibleToGone()
                        binding.prCircular.setVisibleToVisible()
                    }

                    is UiState.Success -> {
                        Log.d("ChattingRoom", "${chatMessages.data}")
                        rvAdapter.itemList = chatMessages.data
                        binding.rvChatMessage.scrollToPosition(chatMessages.data.chatList.lastIndex)
                        rvAdapter.notifyDataSetChanged()
                        binding.rvChatMessage.setVisibleToVisible()
                        binding.prCircular.setVisibleToGone()
                        binding.rvChatMessage.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                            if (bottom < oldBottom) {
                                binding.rvChatMessage.postDelayed({
                                    if (chatMessages.data.chatList.isNotEmpty()) {
                                        binding.rvChatMessage.scrollToPosition(chatMessages.data.chatList.size - 1)
                                    }
                                }, 100)
                            }
                        }

                        rvAdapter.itemClick = object : ChattingRecyclerViewAdapter.ItemClick {
                            override fun itemClick(userId: String) {
                                sharedViewModel.getUserId(userId)
                                val userInfoFragment = UserInfoFragment()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, userInfoFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }

                    is UiState.Error -> {
                        binding.rvChatMessage.setVisibleToGone()
                        binding.prCircular.setVisibleToError()
                        Log.d("error", chatMessages.message)
                    }
                }
            }
        }
    }

    private fun initView() {
        with(binding) {
            rvChatMessage.apply {
                adapter = rvAdapter
                layoutManager = LinearLayoutManager(requireActivity())
            }
            ivSend.setOnClickListener {
                val text = binding.etText.text.toString()
                if (text.isNotBlank()){
                    chattingListModel.run {
                        val userId = currentUserId
                        val userName = currentUsername
                        val url = currentUrl
                        viewModel.sendChat(groupId, userId, text, userName, groupName, url)
                        Log.d("ChattingRoom", "${it}")
                    }
                }
                binding.etText.text.clear()
            }
            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun initData() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.currentUser.collectLatest {
                when (it) {
                    is UiState.Success -> {
                        if (it.data != null) {
                            if (it.data.userId != "") {
                                currentUserId = it.data.userId
                                currentUsername = it.data.userName
                                currentUrl = it.data.userProfileThumbnailUrl
                                with(rvAdapter) {
                                    currentUserId = it.data.userId
                                    currentUrl = it.data.userProfileThumbnailUrl
                                    currentUserName = it.data.userName
                                }
                            }
                            rvAdapter.notifyDataSetChanged()
                        }
                    }

                    is UiState.Loading -> {

                    }

                    is UiState.Error -> {
                        Log.d("ChattingRoom", "${it.message}")
                    }
                }
            }
        }
    }


    private fun showNav() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav.setVisibleToVisible()
    }

    private fun hideNav() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav.setVisibleToGone()
    }
}