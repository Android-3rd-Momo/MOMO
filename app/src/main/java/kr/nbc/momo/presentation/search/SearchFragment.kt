package kr.nbc.momo.presentation.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentSearchBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.group.read.ReadGroupFragment
import kr.nbc.momo.presentation.main.SharedViewModel

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchAdapter = SearchAdapter {
        searchItemOnClick(it)
    }

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val searchViewModel: SearchViewModel by viewModels()

    private var searchCategory: String = ""
    private var searchWorks: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initFlow()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initView(){
        with(binding){
            rvSearchResult.apply {
                adapter = searchAdapter
                layoutManager = LinearLayoutManager(requireActivity())
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // 검색 버튼을 눌렀을 때 호출됨
                    query?.let {
                        searchViewModel.getSearchResult(searchCategory, searchWorks, it)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // 검색어 변경될 때마다 호출됨
                    newText?.let {
                        // 실시간 검색어 처리 로직?
                    }
                    return true
                }
            })
        }
    }
    private fun initFlow(){
        lifecycleScope.launch {
            searchViewModel.searchResult.collectLatest {
                when(it){
                    is UiState.Success -> {
                        searchAdapter.itemList = it.data
                        searchAdapter.notifyDataSetChanged()
                    }
                    is UiState.Loading -> {

                    }
                    is UiState.Error -> {
                        Log.d("Error", it.message)
                    }
                }
            }
        }
    }

    private fun searchItemOnClick(groupModel: GroupModel) {
        sharedViewModel.getGroupId(groupModel.groupId)
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ReadGroupFragment())
            addToBackStack(null)
            commit()
        }
    }
}