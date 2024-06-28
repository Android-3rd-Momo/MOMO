package kr.nbc.momo.presentation.chatting.chattingroom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentChattingRoomBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.hideNav
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible
import kr.nbc.momo.util.showNav

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
        initData()
        observeChatList()
        initView()
    }

    override fun onResume() {
        super.onResume()
        hideNav()
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.setLastViewedChat(chattingListModel.groupId, currentUserId, currentUsername, currentUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        showNav()
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
                        Log.d("ChattingRoom", it.message)
                        makeToastWithString(requireContext(), it.message)
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
                        binding.rvChatMessage.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                            if (bottom < oldBottom) {
                                binding.rvChatMessage.post {
                                    if (chatMessages.data.chatList.isNotEmpty()) {
                                        binding.rvChatMessage.scrollToPosition(chatMessages.data.chatList.lastIndex)
                                    }
                                }
                            }
                        }

                        rvAdapter.itemClick = object : ChattingRecyclerViewAdapter.ItemClick {
                            override fun itemClick(userId: String) {
                                sharedViewModel.getUserId(userId)
                                findNavController().navigate(R.id.action_chattingRoomFragment_to_userInfoFragment)
                            }
                        }
                    }

                    is UiState.Error -> {
                        binding.rvChatMessage.setVisibleToGone()
                        binding.prCircular.setVisibleToError()
                        Log.d("error", chatMessages.message)
                        makeToastWithString(requireContext(), chatMessages.message)
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
                if (text.isNotBlank()) {
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
                findNavController().popBackStack()
            }
            etText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrBlank()) ivSend.setVisibleToGone()
                    else ivSend.setVisibleToVisible()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }

    private fun initData() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.currentUser.collectLatest {
                when (it) {
                    is UiState.Success -> {
                        it.data?.let { data ->
                            if(data.userId.isNotEmpty()){
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
/*                        if (it.data != null) {
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
                        }*/
                    }

                    is UiState.Loading -> {
                        //nothing to do
                    }

                    is UiState.Error -> {
                        //nothing to do
                        Log.d("ChattingRoom", it.message)
                    }
                }
            }
        }
    }
}