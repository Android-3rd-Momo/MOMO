package kr.nbc.momo.presentation.onboarding.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.presentation.onboarding.term.TermFragment
import kr.nbc.momo.presentation.signup.SignUpFragment


@AndroidEntryPoint
class LoginFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener{
            login()
        }

        binding.btnGoogleSignUp.setOnClickListener{
            googleLogin()
        }

        binding.tvWithoutSignin.setOnClickListener {
            val fragmentSignUp = SignUpFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView , fragmentSignUp)
                .addToBackStack(null)  // This allows user to navigate back
                .commit()
            dismiss()
        }

    }

    private fun login(){
        firebaseAuth = FirebaseAuth.getInstance()

        val id = binding.etId.text.toString()
        val password = binding.etPassWord.text.toString()

        if (id.isNotEmpty() && password.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword(id, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val fragmentTerm = TermFragment()
                    fragmentTerm.setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
                    fragmentTerm.show(parentFragmentManager, fragmentTerm.tag)
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "아이디/비밀번호가 틀렸습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "아이디/비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()

        }
    }

    private fun googleLogin(){
        Log.i("loginFragment", "dkanrjdsk")

        firebaseAuth = FirebaseAuth.getInstance()
        //파이어베이스 어스 사용인데 null이라서 다시 시도가 뜸

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        //sign in  intent 호출부분이 없음

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val fragmentTerm = TermFragment()
            fragmentTerm.setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            fragmentTerm.show(parentFragmentManager, fragmentTerm.tag)
            dismiss()

        } else {
            Toast.makeText(requireContext(),"다시시도해주세요",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}