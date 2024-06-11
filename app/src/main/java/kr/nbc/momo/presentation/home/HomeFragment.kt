package kr.nbc.momo.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentHomeBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.create.CreateGroupFragment
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.group.read.ReadGroupFragment
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.util.decryptECB
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var latestGroupListAdapter: LatestGroupListAdapter
    private lateinit var myGroupListAdapter: MyGroupListAdapter
    private lateinit var recommendGroupListAdapter: RecommendGroupListAdapter
    private lateinit var currentUser : String
    private lateinit var currentUserCategory : List<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()
        initGroupList()
        initView()
    }

    override fun onStart() {
        super.onStart()
        Log.d("backStackEntryCount", "${parentFragmentManager.backStackEntryCount}")
        if (parentFragmentManager.backStackEntryCount == 0) {
            viewModel.getGroupList()
        }
        initGroupList()
    }

    private fun initView() {
        binding.floatingBtnCreateGroup.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateGroupFragment())
                .addToBackStack(null)
                .commit()

        }
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //fragment의 수명주기가 해당 상태일 때만 실행되도록 보장
                sharedViewModel.currentUser.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            //todo 로딩
                        }

                        is UiState.Success -> {
                            Log.d("currentUser", state.data.userId)
                            currentUser = state.data.userId
                            currentUserCategory = state.data.typeOfDevelopment + state.data.programOfDevelopment
                        }

                        is UiState.Error -> {
                            Log.d("Error", state.message)
                            currentUser = ""
                            currentUserCategory = listOf()
                        }
                    }
                }
            }
        }
    }

    private fun initGroupList() {
        lifecycleScope.launch {
            viewModel.getGroupList.collect { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        Log.d("UiState", uiState.message)
                    }

                    UiState.Loading -> {
                        // TODO()
                    }

                    is UiState.Success -> {
                        val latestGroupList = uiState.data.filter { it.lastDate >= getCurrentTime() && it.firstDate <= getCurrentTime() }
                            .sortedByDescending {
                                val decrypt = it.groupId.decryptECB()
                                val dateTimeIndex = decrypt.lastIndexOf(" ")
                                decrypt.substring(dateTimeIndex)
                            }
                        latestGroupListAdapter = LatestGroupListAdapter(latestGroupList)
                        binding.rvLatestGroupList.adapter = latestGroupListAdapter
                        binding.rvLatestGroupList.layoutManager = LinearLayoutManager(requireContext())

                        val myGroupList = uiState.data.filter { it.userList.contains(currentUser) }
                        myGroupListAdapter = MyGroupListAdapter(myGroupList)
                        binding.rvMyGroupList.adapter = myGroupListAdapter
                        binding.rvMyGroupList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                        val recommendGroupList = uiState.data
                            .filter {
                                val setA = (it.category.programingLanguage + it.category.developmentOccupations).toSet()
                                val setB = currentUserCategory.toSet()
                                setA.intersect(setB).isNotEmpty()
                            } - myGroupList.toSet()
                        recommendGroupListAdapter = RecommendGroupListAdapter(recommendGroupList)
                        binding.rvRecommendGroupList.adapter = recommendGroupListAdapter
                        binding.rvRecommendGroupList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                        onClick(latestGroupList, myGroupList, recommendGroupList)
                    }
                }

            }
        }
    }

    private fun onClick(latestGroupList: List<GroupModel>, myGroupList: List<GroupModel>, recommendGroupList: List<GroupModel>) {
        latestGroupListAdapter.itemClick = object : LatestGroupListAdapter.ItemClick{
            override fun itemClick(position: Int) {
                val groupId = latestGroupList[position].groupId
                sharedViewModel.getGroupId(groupId)
                val readGroupFragment = ReadGroupFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, readGroupFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        myGroupListAdapter.itemClick = object : MyGroupListAdapter.ItemClick{
            override fun itemClick(position: Int) {
                val groupId = myGroupList[position].groupId
                sharedViewModel.getGroupId(groupId)
                val readGroupFragment = ReadGroupFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, readGroupFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        recommendGroupListAdapter.itemClick = object : RecommendGroupListAdapter.ItemClick{
            override fun itemClick(position: Int) {
                val groupId = recommendGroupList[position].groupId
                sharedViewModel.getGroupId(groupId)
                val readGroupFragment = ReadGroupFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, readGroupFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    fun getCurrentTime() : String {
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(Date().time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}