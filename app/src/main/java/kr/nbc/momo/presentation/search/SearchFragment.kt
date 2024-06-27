package kr.nbc.momo.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
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
import kr.nbc.momo.databinding.FragmentSearchBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.group.read.ReadGroupFragment
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.hideNav
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.setVisibleToError
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible
import kr.nbc.momo.util.showNav

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchAdapter = SearchAdapter {
        searchItemOnClick(it)
    }

    private var queryWord = ""

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val searchViewModel: SearchViewModel by viewModels()

    private val categoryList: List<String> by lazy {
        listOf("선택 안함") + resources.getStringArray(R.array.classification).toList()
    }

    private val worksList: List<String> by lazy {
        listOf("선택 안함") + resources.getStringArray(R.array.chipGroupDevelopmentOccupations).toList()
    }

    private val categorySpinnerAdapter by lazy {
        SearchSpinnerAdapter(requireActivity(), categoryList) // 카테고리 리스트 넣기
    }

    private val worksSpinnerAdapter by lazy {
        SearchSpinnerAdapter(requireActivity(), worksList) // 직무 리스트 넣기
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

    override fun onResume() {
        super.onResume()
        hideNav()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        showNav()
    }

    private fun initView() {
        with(binding) {
            includeNoResult.tvNoResult.setText(R.string.no_search_result)
            ivReturn.setOnClickListener {
                findNavController().popBackStack()
            }
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
                        queryWord = it
                    }
                    if (newText == null) queryWord = ""
                    return true
                }
            })

            searchView.queryHint = getString(R.string.query_hint)

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
                            searchCategory = if (item == "선택 안함") "" else item
                            Log.d("test", searchCategory)
                            searchViewModel.getSearchResult(searchCategory, searchWorks, queryWord)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
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
                            searchWorks = if (item == "선택 안함") "" else item
                            Log.d("test", searchWorks)
                            searchViewModel.getSearchResult(searchCategory, searchWorks, queryWord)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
            }
        }

    }

    private fun initFlow() {
        lifecycleScope.launch {
            searchViewModel.searchResult.collectLatest {
                when (it) {
                    is UiState.Success -> {
                        searchAdapter.itemList = it.data
                        searchAdapter.notifyDataSetChanged()
                        if (it.data.isEmpty()) {
                            binding.rvSearchResult.setVisibleToGone()
                            binding.prCircular.setVisibleToGone()
                            binding.includeNoResult.setVisibleToVisible()
                        } else {
                            binding.rvSearchResult.setVisibleToVisible()
                            binding.prCircular.setVisibleToGone()
                            binding.includeNoResult.setVisibleToGone()
                        }
                    }

                    is UiState.Loading -> {
                        binding.rvSearchResult.setVisibleToGone()
                        binding.prCircular.setVisibleToVisible()
                        binding.includeNoResult.setVisibleToGone()
                    }

                    is UiState.Error -> {
                        makeToastWithString(requireContext(), it.message)
                        binding.rvSearchResult.setVisibleToGone()
                        binding.prCircular.setVisibleToError()
                        binding.includeNoResult.setVisibleToGone()
                        Log.d("Error", it.message)
                    }
                }
            }
        }
    }

    private fun searchItemOnClick(groupModel: GroupModel) {
        sharedViewModel.getGroupId(groupModel.groupId)
        findNavController().navigate(R.id.action_searchFragment_to_readGroupFragment)
    }
}