package kr.nbc.momo.presentation.chatting.chattinglist

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentChattingListBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.dummy.groupIdsDummy
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.chatting.chattingroom.ChattingRoomFragment
import kr.nbc.momo.presentation.main.SharedViewModel

@AndroidEntryPoint
class ChattingListFragment : Fragment() {
    private var _binding: FragmentChattingListBinding? = null
    private val binding get() = _binding!!

    private val chattingListAdapter = ChattingListRecyclerViewAdapter {
        itemOnClick(it)
    }

    private val chattingListViewModel: ChattingListViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initView() {
        with(binding) {
            rvChattingList.apply {
                adapter = chattingListAdapter
                layoutManager = LinearLayoutManager(requireActivity())
            }
        }
    }

    private fun initData() {
        chattingListViewModel.getChattingList(groupIdsDummy)

        viewLifecycleOwner.lifecycleScope.launch {
            chattingListViewModel.chattingList.collectLatest { chattingList ->
                when (chattingList) {
                    is UiState.Loading -> {
                        //todo 로딩
                    }

                    is UiState.Success -> {
                        if(chattingList.data.isNotEmpty()){
                            chattingListAdapter.itemList = chattingList.data
                            Log.d("Check UiState", chattingList.data[0].latestChatMessage)
                            chattingListAdapter.notifyDataSetChanged()
                        }else {
                            //todo 가입한 모임이 없습니다.
                        }
                    }

                    is UiState.Error -> {
                        Log.d("error", chattingList.message)
                    }
                }
            }
        }
    }

    //넘기는 거 미완성(conflict 가능성)
    private fun itemOnClick(chattingListModel: ChattingListModel) {
        sharedViewModel.setGroupIdToGroupChat(chattingListModel)
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ChattingRoomFragment())
            addToBackStack(null)
            commit()
        }

    }

}