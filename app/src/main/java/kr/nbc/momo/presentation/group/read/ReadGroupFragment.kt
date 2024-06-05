package kr.nbc.momo.presentation.group.read

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.api.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.DialogJoinProjectBinding
import kr.nbc.momo.databinding.FragmentReadGroupBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.signup.SignUpFragment
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class ReadGroupFragment : Fragment() {
    private var _binding: FragmentReadGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadGroupViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentUser : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavHide()
        observeUserProfile()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        bottomNavShow()
    }

    private fun bottomNavHide() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.visibility = View.GONE
    }

    private fun bottomNavShow() {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
        nav?.visibility = View.VISIBLE
    }
    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //fragment의 수명주기가 해당 상태일 때만 실행되도록 보장
                sharedViewModel.currentUser.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            //todo 로딩
                        }

                        is UiState.Success -> {
                            Log.d("currentUser", state.data.userId)
                            currentUser = state.data.userId
                            initGroup()
                        }

                        is UiState.Error -> {
                            Log.d("error", state.message)
                            initGroup()
                        }
                    }
                }
            }
        }
    }

    private fun initGroup() {
        lifecycleScope.launch {
            sharedViewModel.groupId.observe(viewLifecycleOwner) {
                if (it != null) {
                    viewModel.readGroup(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.readGroup.collect { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        Log.d("UiState", uiState.message)
                    }

                    UiState.Loading -> {
                        // TODO()
                    }

                    is UiState.Success -> {
                        initView(uiState.data)
                    }
                }
            }
        }
    }

    private fun initView(data: GroupModel) {
        with(binding) {
            ivGroupImage.load(data.groupThumbnail)
            tvGroupName.text = data.groupName
            tvGroupOneLineDescription.text = data.groupOneLineDescription
            tvGroupDescription.text = data.groupDescription
            tvFirstDate.text = data.firstDate
            tvLastDate.text = data.lastDate
            tvLeaderId.text = data.leaderId

            if (data.categoryList.contains(categoryBack.text)) categoryBack.setVisibleToVisible()
            if (data.categoryList.contains(categoryFront.text)) categoryFront.setVisibleToVisible()
            if (data.categoryList.contains(categoryPull.text)) categoryPull.setVisibleToVisible()

            btnClickListener(currentUser, data)
        }

    }

    private fun btnClickListener(currentUser: String?, data: GroupModel) {
        if (currentUser == null) {
            binding.btnJoinProject.setOnClickListener {
                showDialog(false, false)
            }
        } else {
            if (data.userList.contains(currentUser)) {
                binding.btnJoinProject.setOnClickListener {
                    showDialog(true, true)
                }
            } else {
                binding.btnJoinProject.setOnClickListener {
                    showDialog(true, false)
                }
            }
        }
    }

    private fun showDialog(loginBoolean: Boolean, containUserList: Boolean) {
        val dialogBinding = DialogJoinProjectBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        if (loginBoolean) {
            if (containUserList) {
                // TODO 채팅룸 이동
            } else {
                dialogBinding.btnConfirm.setOnClickListener {
                    dialog.dismiss()
                    // TODO 그룹의 유저리스트의 현재 유저정보 추가
                }
            }

        } else {
            dialogBinding.tvClose.text = "로그인페이지로 이동합니다."
            dialogBinding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                val signUpFragment = SignUpFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, signUpFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
}

