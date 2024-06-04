package kr.nbc.momo.presentation.onboarding.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.databinding.FragmentSignUpBinding
import kr.nbc.momo.presentation.onboarding.term.TermFragment
import kr.nbc.momo.presentation.signup.SignUpFragment
import kr.nbc.momo.presentation.signup.SignUpViewModel


class LoginFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener{
            val fragmentTerm = TermFragment()
            fragmentTerm.setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            fragmentTerm.show(parentFragmentManager, fragmentTerm.tag)
            dismiss()
        }

        binding.tvWithoutSignin.setOnClickListener{
            val fragmentSignUp = SignUpFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragmentSignUp)
                .addToBackStack(null)
                .commit()
            dismiss()
        }
    }

}