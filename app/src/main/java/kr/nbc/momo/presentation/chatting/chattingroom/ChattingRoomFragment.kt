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
    //bundle로 던지든 공유뷰모델에 넣든 리스트에서 선택한 그룹아이디 받아오기(얘네도 정보 플로우 이용해서 갱신해야함)
    private var groupId = ""
    private var groupName = ""

    //공유 뷰모델에서 로그인 정보 받아오기
    private var userId = ""
    private var userName = ""
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
        hideNav()
        initData()
        observeChatList()
        initView()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.setLastViewedChat(groupId, userId, userName)
    }

    override fun onDestroyView() {
        showNav()
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.removeGroupIdToGroupChat()
    }

    private fun observeChatList() {
        lifecycleScope.launch {
            viewModel.chatMessages.collectLatest { chatMessages ->
                when (chatMessages) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        rvAdapter.itemList = chatMessages.data
                        binding.rvChatMessage.scrollToPosition(chatMessages.data.chatList.lastIndex)
                        rvAdapter.notifyDataSetChanged()
                        Log.d("ChattingRoom", "${chatMessages.data}")
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
                viewModel.sendChat(groupId, userId, text, userName, groupName)
                binding.etText.text.clear()
            }
            tvTitle.text = groupName
            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun initData() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.groupIdToGroupChat.collectLatest {
                Log.d("group", "$it")
                chattingListModel = it ?: ChattingListModel()
                groupId = chattingListModel.groupId
                groupName = chattingListModel.groupName
                viewModel.getChatMessages(groupId)
            }
            sharedViewModel.currentUser.collectLatest {
                when (it) {
                    is UiState.Success -> {
                        userId = it.data.userId
                        userName = it.data.userName
                        rvAdapter.currentUserId = it.data.userId
                        rvAdapter.notifyDataSetChanged()
                    }

                    is UiState.Loading -> {}
                    is UiState.Error -> {}
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