package kr.nbc.momo.presentation.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentSignUpBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.mypage.MyPageFragment
import kr.nbc.momo.presentation.signup.model.UserModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignUpViewModel by viewModels()

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

            //todo id 추가 및 중복 예외처리
            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || number.isEmpty() || id.isEmpty()) {
                Snackbar.make(binding.root, "입력하지 않은 항목이 있습니다.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
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