package kr.nbc.momo.presentation.onboarding.signup

import android.content.Intent
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentSignUpBinding
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.main.SharedViewModel
import kr.nbc.momo.presentation.onboarding.developmentType.DevelopmentActivity
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.util.makeToastWithStringRes

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
                binding.etEmail.error = getString(R.string.email_blank_error)
                isValid = false
            } else if (!isValidEmail(email)) {
                binding.etEmail.error = getString(R.string.email_regex_error)
                isValid = false
            } else {
                binding.etEmail.error = null
            }

            if (password.isEmpty()) {
                binding.etPassWord.error = getString(R.string.password_blank_error)
                isValid = false
            } else if (!isValidPassword(password)) {
                binding.etPassWord.error = getString(R.string.password_regex_error)
                isValid = false
            } else {
                binding.etPassWord.error = null
            }

            if(checkPassword.isEmpty()){
                binding.etCheckPassWord.error = getString(R.string.password_blank_error)
                isValid = false
            }else if (password != checkPassword) {
                binding.etCheckPassWord.error = getString(R.string.password_check_error)
                isValid = false
            } else {
                binding.etCheckPassWord.error = null
            }

            if (name.isEmpty()) {
                binding.etName.error = getString(R.string.name_blank_error)
                isValid = false
            } else if (!isValidName(name)) {
                binding.etName.error = getString(R.string.name_regex_error)
                isValid = false
            } else {
                binding.etName.error = null
            }

            if (number.isEmpty()) {
                binding.etNumber.error = getString(R.string.phone_blank_error)
                isValid = false
            } else if (!isNumberChecked) {
                binding.etNumber.error = getString(R.string.phone_check_error)
                isValid = false
            } else {
                binding.etNumber.error = null
            }

            if (!isIdChecked) {
                binding.etId.error = getString(R.string.id_check_error)
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
                binding.etId.error = getString(R.string.id_blank_error)
            } else if (!isValidId(id)) {
                binding.etId.error = getString(R.string.id_regex_error)
            } else {
                lifecycleScope.launch {
                    try {
                        val isDuplicate = viewModel.isUserIdDuplicate(id)
                        if (isDuplicate) {
                            binding.etId.error = getString(R.string.id_duplication_error)
                        } else {
                            binding.etId.error = null
                            makeToastWithStringRes(requireContext(), R.string.id_can_use)
                            //Toast.makeText(requireContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
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
                binding.etNumber.error = getString(R.string.phone_blank_error)
            } else if (!isValidPhoneNumber(number)){
                binding.etNumber.error = getString(R.string.phone_regex_error)
            }
            else {

                lifecycleScope.launch {
                    try {

                        val isDuplicate = viewModel.isUserNumberDuplicate(number)
                        if (isDuplicate) {
                            binding.etNumber.error = getString(R.string.phone_duplication_error)
                        } else {
                            binding.etNumber.error = null
                            makeToastWithStringRes(requireContext(), R.string.phone_can_use)
                            //Toast.makeText(requireContext(), "사용 가능한 전화번호입니다.", Toast.LENGTH_SHORT).show()
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
                        makeToastWithStringRes(requireContext(), R.string.sign_up_success)
                        //Toast.makeText(requireContext(), "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireActivity(), DevelopmentActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    is UiState.Error -> {
                        Log.d("SignUp Error", state.message)
                        if (state.message.contains("The email address is already in use by another account")) {
                            binding.etEmail.error = getString(R.string.email_duplication_error)
                        } else {
                            Log.d("SignUp error",state.message)
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

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneNumberPattern = "^\\d{3}-\\d{4}-\\d{4}$"
        return phoneNumber.matches(phoneNumberPattern.toRegex())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}