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
import kr.nbc.momo.presentation.signup.SignUpFragment


@AndroidEntryPoint
class SignInFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val signInViewModel: SignInViewModel by viewModels()

    //private lateinit var mGoogleSignInClient: GoogleSignInClient

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

        login()
        observeLoginViewModel()
        usingAppWithoutSignIn()

    }

    private fun login() {
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
    }
    private fun observeLoginViewModel(){
        lifecycleScope.launch {
            signInViewModel.authState.collect {uiState ->
                when (uiState){
                    is UiState.Loading -> {

                    }
                    is UiState.Success -> {
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is UiState.Error -> {
                        Snackbar.make(binding.root, "이메일 또는 비밀번호를 다시 입력해주세요", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun usingAppWithoutSignIn() {
        binding.tvWithoutSignin.setOnClickListener {
            val fragmentSignUp = SignUpFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragmentSignUp)
                .addToBackStack(null)  // This allows user to navigate back
                .commit()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //private fun googleLogin() {
    //firebaseAuth = FirebaseAuth.getInstance()
    //파이어베이스 어스 사용인데 null이라서 다시 시도가 뜸

    //val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    //    .requestIdToken(getString(R.string.default_web_client_id))
    //    .requestEmail()
    //   .build()

    //mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

    //sign in  intent 호출부분이 없음

    //val auth = Firebase.auth
    //val user = auth.currentUser

    //if (user != null) {
    //val fragmentTerm = TermFragment()
    //fragmentTerm.setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
    //fragmentTerm.show(parentFragmentManager, fragmentTerm.tag)
    //dismiss()

    //} else {
    // Toast.makeText(requireContext(), "다시시도해주세요", Toast.LENGTH_SHORT).show()
    //}

    //


}
