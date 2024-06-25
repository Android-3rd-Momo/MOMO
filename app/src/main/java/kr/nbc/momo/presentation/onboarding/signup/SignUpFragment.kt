package kr.nbc.momo.presentation.onboarding.signup


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import kr.nbc.momo.presentation.onboarding.developmentType.DevelopmentFragment
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.util.addValidationTextWatcher
import kr.nbc.momo.util.checkDuplicate
import kr.nbc.momo.util.isValidEmail
import kr.nbc.momo.util.isValidId
import kr.nbc.momo.util.isValidName
import kr.nbc.momo.util.isValidPassword
import kr.nbc.momo.util.isValidPhoneNumber
import kr.nbc.momo.util.makeToastWithStringRes

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignUpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isIdChecked = false
    private var isNumberChecked = false

    private val validMap = mutableMapOf<EditText, Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
        initCheckDuplicate()
        setUpTextWatcher()
        setUpSignUpButtonState()
    }


    private fun initView() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassWord.text.toString()
            val name = binding.etName.text.toString()
            val number = binding.etNumber.text.toString()
            val id = binding.etId.text.toString()

            val user = UserModel(email, name, number, id)
            viewModel.signUp(email, password, user)
        }
    }

    private fun setUpTextWatcher() {
        binding.etEmail.addValidationTextWatcher(
            String::isValidEmail,
            getString(R.string.email_regex_error),
            validMap,
            binding.btnSignUp
        )
        binding.etPassWord.addValidationTextWatcher(
            String::isValidPassword,
            getString(R.string.password_regex_error),
            validMap,
            binding.btnSignUp
        )
        binding.etCheckPassWord.addValidationTextWatcher(
            { it.isNotEmpty() && it == binding.etPassWord.text.toString() },
            getString(R.string.password_check_error),
            validMap,
            binding.btnSignUp
        )
        binding.etName.addValidationTextWatcher(
            String::isValidName,
            getString(R.string.name_regex_error),
            validMap,
            binding.btnSignUp
        )
        binding.etId.addValidationTextWatcher(
            String::isValidId,
            getString(R.string.id_regex_error),
            validMap,
            binding.btnCheckId
        )
        binding.etNumber.addValidationTextWatcher(
            String::isValidPhoneNumber,
            getString(R.string.phone_regex_error),
            validMap,
            binding.btnCheckNumber
        )

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etId.hasFocus()) {
                    isIdChecked = false
                }
                if (binding.etNumber.hasFocus()) {
                    isNumberChecked = false
                }
                setUpSignUpButtonState()
                setUpDuplicateCheck()
            }

            override fun afterTextChanged(s: Editable?) {
                //No action needed
            }
        }
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassWord.addTextChangedListener(textWatcher)
        binding.etCheckPassWord.addTextChangedListener(textWatcher)
        binding.etName.addTextChangedListener(textWatcher)
        binding.etId.addTextChangedListener(textWatcher)
        binding.etNumber.addTextChangedListener(textWatcher)
    }


    private fun initCheckDuplicate() {
        binding.btnCheckId.setOnClickListener {
            checkDuplicate(
                String::isValidId,
                getString(R.string.id_regex_error),
                validMap,
                viewModel::isUserIdDuplicate,
                binding.etId,
                {
                    isIdChecked = true
                    setUpSignUpButtonState()
                    setUpDuplicateCheck()
                },
                R.string.id_can_use
            )
        }
        binding.btnCheckNumber.setOnClickListener {
            checkDuplicate(
                String::isValidPhoneNumber,
                getString(R.string.phone_regex_error),
                validMap,
                viewModel::isUserNumberDuplicate,
                binding.etNumber,
                {
                    isNumberChecked = true
                    setUpSignUpButtonState()
                    setUpDuplicateCheck()
                },
                R.string.phone_can_use
            )
        }
    }

    private fun setUpSignUpButtonState() {
        val allFieldsValid = validMap.values.all { it }
        val allChecksPassed = isIdChecked && isNumberChecked
        val areAllFieldsNotEmpty = listOf(
            binding.etEmail,
            binding.etPassWord,
            binding.etCheckPassWord,
            binding.etName,
        ).all { it.text.toString().isNotEmpty() }
        val isSignUpEnabled = allFieldsValid && allChecksPassed && areAllFieldsNotEmpty
        binding.btnSignUp.isEnabled = isSignUpEnabled
    }

    private fun setUpDuplicateCheck() {
        val etId = binding.etId.text?.isNotEmpty() ?: false
        val etNumber = binding.etNumber.text?.isNotEmpty() ?: false
        val shouldShowNotifyDuplicate = (etId && !isIdChecked) || (etNumber && !isNumberChecked)
        binding.tvNotifyDuplicate.visibility = if (shouldShowNotifyDuplicate) View.VISIBLE else View.GONE
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
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, DevelopmentFragment())
                            .addToBackStack(null)
                            .commit()
                    }

                    is UiState.Error -> {
                        if (state.message.contains("The email address is already in use by another account")) {
                            binding.etEmail.error = getString(R.string.email_duplication_error)
                        } else {
                            makeToastWithStringRes(requireContext(), R.string.sign_up_failed)
                        }
//                        binding.btnSignUp.isEnabled = true
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