package kr.nbc.momo.presentation.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentSignUpBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.term.TermFragment
import kr.nbc.momo.presentation.signup.model.UserModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignUpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignUp()
        initObservers()
    }


    private fun initSignUp() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassWord.text.toString()
            val name = binding.etName.text.toString()
            val number = binding.etNumber.text.toString()
            val id = binding.etId.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || number.isEmpty() || id.isEmpty()) {
                Snackbar.make(binding.root, "입력하지 않은 항목이 있습니다.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val fragmentTerm = TermFragment()
                fragmentTerm.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
                fragmentTerm.show(parentFragmentManager, fragmentTerm.tag)
            }

            val user = UserModel(email, name, number, id)
            viewModel.signUp(email, password, user)
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        //로딩 처리
                    }

                    is UiState.Success -> {
                        sharedViewModel.updateUser(state.data)
                        Snackbar.make(binding.root, "회원가입에 성공하였습니다.", Snackbar.LENGTH_SHORT).show()

                    }

                    is UiState.Error -> {
                        Log.d("SignUp Error", state.message)
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
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