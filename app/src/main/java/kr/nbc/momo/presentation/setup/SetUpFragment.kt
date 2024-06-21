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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentSetUpBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.util.hideNav
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible
import kr.nbc.momo.util.showNav

@AndroidEntryPoint
class SetUpFragment : Fragment() {
    private var _binding: FragmentSetUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SetUpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var user: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNav()
        observeUserProfile()
        observerSearchLeader()
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
                            state.data?.let { eachEventHandler(it.userId) }
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

    private fun observerSearchLeader() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchLeaderState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            // Handle loading state
                        }
                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                showConfirmationDialog(getString(R.string.check_withdrawal)) {
                                    viewModel.withdrawal()
                                    sharedViewModel.getCurrentUser()
                                }
                            } else {
                                makeToastWithString(requireContext(), state.data.joinToString().plus(getString(R.string.you_are_leader)))
                                //Toast.makeText(requireContext(), state.data.joinToString().plus(getString(R.string.you_are_leader)), Toast.LENGTH_SHORT).show()
                            }
                        }
                        is UiState.Error -> {
                            // Handle error state
                        }
                    }
                }
            }
        }
    }

    private fun eachEventHandler(userId: String) {
        with(binding){
            ivReturn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            tvSignOut.setOnClickListener {
                showConfirmationDialog(getString(R.string.check_logout)) {
                    viewModel.signOut()
                    sharedViewModel.getCurrentUser()
                }
            }
            tvWithdrawal.setOnClickListener {
                viewModel.searchLeader(userId)
            }
        }
    }

    private fun showConfirmationDialog(message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                onConfirm()
                goOnboarding()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
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
        showNav()
        _binding = null
    }
}