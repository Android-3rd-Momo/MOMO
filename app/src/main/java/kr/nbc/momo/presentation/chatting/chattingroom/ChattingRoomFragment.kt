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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        _binding = null
        super.onDestroyView()
    }

    private fun observeGroupId() {
        sharedViewModel.groupId.observe(viewLifecycleOwner) {
            it?.let { groupId ->
                viewModel.getChatListItemById(groupId)
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

                    }

                    is UiState.Success -> {
                        Log.d("ChattingRoom", "${chatMessages.data}")
                        rvAdapter.itemList = chatMessages.data
                        binding.rvChatMessage.scrollToPosition(chatMessages.data.chatList.lastIndex)
                        rvAdapter.notifyDataSetChanged()
                    }

                    is UiState.Error -> {
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
                chattingListModel.run {
                    val userId = currentUserId
                    val userName = currentUsername
                    val url = currentUrl
                    viewModel.sendChat(groupId, userId, text, userName, groupName, url)
                    Log.d("ChattingRoom", "${it}")
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
                        if (it.data.userId != "") {
                            currentUserId = it.data.userId
                            rvAdapter.currentUserId = it.data.userId
                            Log.d("ChattingRoom", "${it.data}")
                        }
                        if (it.data.userName != "") currentUsername = it.data.userName
                        currentUrl = it.data.userProfileThumbnailUrl
                        rvAdapter.notifyDataSetChanged()
                    }

                    is UiState.Loading -> {}
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