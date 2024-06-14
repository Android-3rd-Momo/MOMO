package kr.nbc.momo.presentation.onboarding.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.onboarding.signup.SignUpFragment


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
        binding.btnSignUp.setOnClickListener {
            val email = binding.etId.text.toString()
            val password = binding.etPassWord.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "이메일 또는 비밀번호를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                signInViewModel.signIn(email, password)
            }
        }

        binding.btnSignIn.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SignUpFragment())
                .commit()
            dismiss()
        }
    }
    private fun observeLoginViewModel(){
        lifecycleScope.launch {
            signInViewModel.authState.collect {uiState ->
                when (uiState){
                    is UiState.Loading -> {

                    }
                    is UiState.Success -> {
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    is UiState.Error -> {
                        Snackbar.make(binding.root, "이메일 또는 비밀번호를 다시 입력해주세요", Snackbar.LENGTH_SHORT).show()
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
