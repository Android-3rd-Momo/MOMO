package kr.nbc.momo.presentation.onboarding.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignUpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isIdChecked = false
    private var isNumberChecked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignUp()
        initObservers()
        initCheckId()
        initCheckNumber()
        setUpTextWatch()
    }

    private fun setUpTextWatch() {
        binding.etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //No action needed
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isIdChecked = false
                binding.etId.error = null
            }

            override fun afterTextChanged(p0: Editable?) {
                //No action needed
            }

        })
        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //No action needed
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isNumberChecked = false
                binding.etNumber.error = null
            }

            override fun afterTextChanged(p0: Editable?) {
                //No action needed
            }

        })
    }


    private fun initSignUp() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassWord.text.toString()
            val checkPassword = binding.etCheckPassWord.text.toString()
            val name = binding.etName.text.toString()
            val number = binding.etNumber.text.toString()
            val id = binding.etId.text.toString()

            var isValid = true

            //입력하지 않았을 경우, 유효성 실패, 중복 확인

            if (email.isEmpty()) {
                binding.etEmail.error = "이메일을 입력해주세요."
                isValid = false
            } else if (!isValidEmail(email)) {
                binding.etEmail.error = "이메일 형식이 올바르지 않습니다."
                isValid = false
            } else {
                binding.etEmail.error = null
            }

            if (password.isEmpty()) {
                binding.etPassWord.error = "비밀번호를 입력해주세요."
                isValid = false
            } else if (!isValidPassword(password)) {
                binding.etPassWord.error = "8-20자의 영문과 숫자를 함께 사용해야합니다."
                isValid = false
            } else {
                binding.etPassWord.error = null
            }

            if(checkPassword.isEmpty()){
                binding.etCheckPassWord.error = "비밀번호를 입력해주세요."
                isValid = false
            }else if (password != checkPassword) {
                binding.etCheckPassWord.error = "비밀번호가 일치하지 않습니다."
                isValid = false
            } else {
                binding.etCheckPassWord.error = null
            }

            if (name.isEmpty()) {
                binding.etName.error = "이름을 입력해주세요."
                isValid = false
            } else if (!isValidName(name)) {
                binding.etName.error = "3-10자의 영문자나 한글만 가능합니다."
                isValid = false
            } else {
                binding.etName.error = null
            }

            if (number.isEmpty()) {
                binding.etNumber.error = "전화번호를 입력해주세요."
                isValid = false
            } else if (!isNumberChecked) {
                binding.etNumber.error = "전화번호 중복을 확인해주세요."
                isValid = false
            } else {
                binding.etNumber.error = null
            }

            if (!isIdChecked) {
                binding.etId.error = "아이디 중복을 확인해주세요."
                isValid = false
            } else {
                binding.etId.error = null
            }
            if (isValid) {
                val user = UserModel(email, name, number, id)
                viewModel.signUp(email, password, user)
            }
        }
    }

    private fun initCheckId() {
        binding.btnCheckId.setOnClickListener {
            val id = binding.etId.text.toString()
            if (id.isEmpty()) {
                binding.etId.error = "아이디를 입력해주세요."
            } else if (!isValidId(id)) {
                binding.etId.error = "3-10자의 영문자,한글 숫자만 가능합니다."
            } else {
                lifecycleScope.launch {
                    try {
                        val isDuplicate = viewModel.isUserIdDuplicate(id)
                        if (isDuplicate) {
                            binding.etId.error = "이미 사용 중인 아이디입니다."
                        } else {
                            binding.etId.error = null
                            Snackbar.make(binding.root, "사용 가능한 아이디입니다.", Snackbar.LENGTH_SHORT).show()
                            isIdChecked = true
                        }
                    } catch (e: Exception) {
                        Log.e("SignUp", "Failed to Check UserId Validity", e)
                    }
                }
            }
        }
    }

    private fun initCheckNumber() {
        binding.btnCheckNumber.setOnClickListener {
            val number = binding.etNumber.text.toString()
            if (number.isEmpty()) {
                binding.etNumber.error = "전화번호를 입력해주세요."
            } else {

                lifecycleScope.launch {
                    try {

                        val isDuplicate = viewModel.isUserNumberDuplicate(number)
                        if (isDuplicate) {
                            binding.etNumber.error = "이미 사용 중인 전화번호입니다."
                        } else {
                            binding.etNumber.error = null
                            Snackbar.make(binding.root, "사용 가능한 전화번호입니다.", Snackbar.LENGTH_SHORT).show()
                            isNumberChecked = true
                        }
                    } catch (e: Exception) {
                        Log.e("SignUp", "Failed to Check UserNumber Validity", e)
                    }
                }
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        //No action needed
                    }

                    is UiState.Success -> {
                        sharedViewModel.updateUser(state.data)
                        Snackbar.make(binding.root, "회원가입에 성공하였습니다.", Snackbar.LENGTH_SHORT).show()

                        val fragmentTerm = TermFragment()
                        fragmentTerm.setStyle(
                            BottomSheetDialogFragment.STYLE_NORMAL,
                            R.style.AppBottomSheetDialogBorder20WhiteTheme
                        )
                        fragmentTerm.show(parentFragmentManager, fragmentTerm.tag)

                    }

                    is UiState.Error -> {
                        Log.d("SignUp Error", state.message)
                        if (state.message.contains("The email address is already in use by another account")) {
                            binding.etEmail.error = "이미 가입된 이메일 입니다."
                        } else {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }

    private fun isValidName(userName: String): Boolean {
        val usernamePattern = "^[A-Za-z가-힣]{3,20}$"
        return userName.matches(usernamePattern.toRegex())
    }

    private fun isValidId(userId: String): Boolean {
        val usernamePattern = "^[A-Za-z가-힣0-9]{3,20}$"
        return userId.matches(usernamePattern.toRegex())
    }

    private fun isValidPassword(password: String): Boolean { //8~20, 영문 + 숫자
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,20}$"
        return password.matches(passwordPattern.toRegex())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}