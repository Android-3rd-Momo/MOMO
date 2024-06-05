package kr.nbc.momo.presentation.onboarding.term

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.databinding.FragmentTermBinding
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.onboarding.UserViewModel

@AndroidEntryPoint
class TermFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTermBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTermBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAccept.setOnClickListener{
            userViewModel.updateUser { it.copy(termsAccepted = true) }
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            dismiss()
        }
    }

}