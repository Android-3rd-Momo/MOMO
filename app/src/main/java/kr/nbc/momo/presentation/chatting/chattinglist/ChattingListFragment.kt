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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentChattingListBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.chatting.chattingroom.ChattingRoomFragment
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

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
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.currentUser.collectLatest {
                when (it) {
                    is UiState.Loading -> {
                        binding.prCircular.setVisibleToVisible()
                        binding.includeNoResult.setVisibleToGone()
                    }

                    is UiState.Success -> {
                        binding.prCircular.setVisibleToGone()
                        binding.includeNoResult.setVisibleToGone()
                        if (it.data != null) {
                            binding.prCircular.setVisibleToVisible()
                            binding.rvChattingList.setVisibleToGone()
                            binding.includeNoResult.setVisibleToGone()
                            chattingListViewModel.getChattingList(it.data.userGroup, it.data.userId)
                        } else {
                            //로그인 안되어 있을 경우
                            binding.includeNoResult.tvNoResult.setText(R.string.need_login)
                            binding.includeNoResult.setVisibleToVisible()
                        }
                    }

                    is UiState.Error -> {
                        binding.includeNoResult.setVisibleToVisible()
                        binding.prCircular.setVisibleToGone()
                        Log.d("error", it.message)
                        makeToastWithString(requireContext(), it.message)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            chattingListViewModel.chattingList.collectLatest { chattingList ->
                when (chattingList) {
                    is UiState.Loading -> {
                        //nothing to do
                    }

                    is UiState.Success -> {
                        if (chattingList.data.isNotEmpty()) {
                            chattingListAdapter.itemList = chattingList.data
                            chattingListAdapter.notifyDataSetChanged()
                            binding.prCircular.setVisibleToGone()
                            binding.rvChattingList.setVisibleToVisible()
                            binding.includeNoResult.setVisibleToGone()
                        } else {
                            binding.prCircular.setVisibleToGone()
                            binding.rvChattingList.setVisibleToGone()
                            binding.includeNoResult.tvNoResult.setText(R.string.empty_joined_group)
                            binding.includeNoResult.setVisibleToVisible()
                        }
                    }

                    is UiState.Error -> {
                        binding.prCircular.setVisibleToError()
                        binding.rvChattingList.setVisibleToGone()
                        binding.includeNoResult.setVisibleToGone()
                        Log.d("error", chattingList.message)
                        makeToastWithString(requireContext(), chattingList.message)
                    }
                }
            }
        }
    }

    private fun itemOnClick(chattingListModel: ChattingListModel) {
        sharedViewModel.getGroupId(chattingListModel.groupId)
        findNavController().navigate(R.id.action_chattingListFragment_to_chattingRoomFragment)
    }
}