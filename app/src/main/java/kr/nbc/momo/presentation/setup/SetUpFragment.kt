package kr.nbc.momo.presentation.setup

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.databinding.FragmentSetUpBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.GetStartedActivity

@AndroidEntryPoint
class SetUpFragment : Fragment() {
    private var _binding: FragmentSetUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SetUpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()
        eachEventHandler()
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentUser.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            // Handle loading state
                        }
                        is UiState.Success -> {
                            // Handle success state
                            val user = state.data
                            // Update UI with user info if needed
                        }
                        is UiState.Error -> {
                            // Handle error state
                        }
                    }
                }
            }
        }
    }

    private fun eachEventHandler(){ //todo 임시 dialog
        binding.btnSignOut.setOnClickListener {
            showConfirmationDialog("로그아웃 하시겠습니까?") {
                viewModel.signOut()
                sharedViewModel.getCurrentUser()
            }
        }

        binding.btnWithdrawal.setOnClickListener {
            showConfirmationDialog("회원탈퇴 하시겠습니까?") {
                viewModel.withdrawal()
                sharedViewModel.getCurrentUser()
            }
        }
    }

    private fun showConfirmationDialog(message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                onConfirm()
                goOnboarding()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun goOnboarding(){
        val intent = Intent(activity, GetStartedActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}