package kr.nbc.momo.presentation.onboarding.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.onboarding.term.TermFragment
import kr.nbc.momo.util.makeToastWithString
import kr.nbc.momo.util.makeToastWithStringRes


@AndroidEntryPoint
class SignInFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
        observeLoginViewModel()
    }

    private fun setOnClickListener() {
        with(binding){
            btnSignUp.setOnClickListener {
                val email = etId.text.toString()
                val password = etPassWord.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    makeToastWithStringRes(requireContext(), R.string.email_or_password_blank_error)
                    return@setOnClickListener
                } else {
                    signInViewModel.signIn(email, password)
                }
            }

            btnSignIn.setOnClickListener {
                val fragmentTerm = TermFragment()
                fragmentTerm.setStyle(
                    STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
                fragmentTerm.show(parentFragmentManager, fragmentTerm.tag)
                dismiss()
            }
        }

    }

    private fun observeLoginViewModel() {
        lifecycleScope.launch {
            signInViewModel.authState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }

                    is UiState.Error -> {
                        val errorMessage = when {
                            uiState.message.contains("network") -> getString(R.string.network_error)
                            uiState.message.contains("password") -> getString(R.string.email_or_password_error)
                            else -> getString(R.string.unknown_error)
                        }
                        makeToastWithString(requireContext(), errorMessage)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
