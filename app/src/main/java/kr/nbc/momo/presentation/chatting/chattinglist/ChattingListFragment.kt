package kr.nbc.momo.presentation.chatting.chattinglist

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentChattingListBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattingroom.ChattingRoomFragment
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
        showNav()
        _binding = null
        super.onDestroyView()
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
        chattingListViewModel.getChattingList(listOf())

        viewLifecycleOwner.lifecycleScope.launch {
            chattingListViewModel.chattingList.collectLatest { chattingList ->
                when (chattingList) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        chattingListAdapter.itemList = chattingList.data
                    }

                    is UiState.Error -> {
                        Log.d("error", chattingList.message)
                    }
                }
            }
        }
    }

    //넘기는 거 미완성(conflict 가능성)
    private fun itemOnClick(groupId: String) {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ChattingRoomFragment())
            addToBackStack(null)
            commit()
        }
        hideNav()
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