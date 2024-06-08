package kr.nbc.momo.presentation.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

    private val testList: List<String> = listOf(
        "test1", "test2", "test3"
    )

    private val categorySpinnerAdapter by lazy{
        SearchSpinnerAdapter(requireActivity(), testList) // 카테고리 리스트 넣기
    }

    private val worksSpinnerAdapter by lazy{
        SearchSpinnerAdapter(requireActivity(), testList) // 직무 리스트 넣기
    }

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

            spCategory.apply {
                adapter = categorySpinnerAdapter
                onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val item = (parent?.adapter?.getItem(position) as? String) ?: ""
                        searchCategory = item
                        searchViewModel.getSearchResult(searchCategory, searchWorks, "")
                        Log.d("test", "$searchCategory, $searchWorks")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        searchCategory = ""
                        searchViewModel.getSearchResult(searchCategory, searchWorks, "")
                        Log.d("test", "$searchCategory, $searchWorks")
                    }
                }
            }
            spWorks.apply {
                adapter = worksSpinnerAdapter
                onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val item = (parent?.adapter?.getItem(position) as? String) ?: ""
                            searchWorks = item
                            searchViewModel.getSearchResult(searchCategory, searchWorks, "")
                            Log.d("test", "$searchCategory, $searchWorks")
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            searchWorks = ""
                            searchViewModel.getSearchResult(searchCategory, searchWorks, "")
                            Log.d("test", "$searchCategory, $searchWorks")
                        }
                    }
            }
        }

    }
    private fun initFlow(){
        lifecycleScope.launch {
            searchViewModel.searchResult.collectLatest {
                when(it){
                    is UiState.Success -> {
                        searchAdapter.itemList = it.data
                        searchAdapter.notifyDataSetChanged()
                        Log.d("test", it.data.toString())
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