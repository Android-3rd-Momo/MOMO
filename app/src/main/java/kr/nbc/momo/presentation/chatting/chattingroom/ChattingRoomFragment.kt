package kr.nbc.momo.presentation.chatting.chattingroom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentChattingRoomBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class ChattingRoomFragment : Fragment() {
    private var _binding: FragmentChattingRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChattingRoomViewModel by viewModels()

    //bundle로 던지든 공유뷰모델에 넣든 리스트에서 선택한 그룹아이디 받아오기(얘네도 정보 플로우 이용해서 갱신해야함)
    private val groupId = "group_id"
    private val groupName = "테스트 그룹"

    //공유 뷰모델에서 로그인 정보 받아오기
    private val userId = "user_id"
    private val userName = "test_name"
    private val rvAdapter = ChattingRecyclerViewAdapter(userId)

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

    override fun onDestroyView() {
        showNav()
        _binding = null
        super.onDestroyView()
    }

    private fun observeChatList() {
        lifecycleScope.launch {
            viewModel.chatMessages.collect { chatMessages ->
                when (chatMessages) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        rvAdapter.itemList = chatMessages.data
                        binding.rvChatMessage.scrollToPosition(chatMessages.data.chatList.lastIndex)
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
            btn1.setOnClickListener {
                val text = binding.etText.text.toString()
                viewModel.sendChat(groupId, userId, text, userName, groupName)
                binding.etText.text.clear()
            }
        }
    }

    private fun initData() {
        viewModel.getChatMessages(groupId)
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