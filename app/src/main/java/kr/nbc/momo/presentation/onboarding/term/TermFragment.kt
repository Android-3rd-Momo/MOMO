package kr.nbc.momo.presentation.onboarding.term

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTermBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allTermCheck()
        handleOnBackPressed()
        termAceppt()

        //선택약관 동의시에 카카오톡 알람을 주게 하는 메소드 만들기
    }

    private fun allTermCheck() {
        binding.cbAllAccept.setOnCheckedChangeListener { _, isChecked ->
            binding.cbTerm1.isChecked = isChecked
            binding.cbTerm2.isChecked = isChecked
            binding.cbTerm3.isChecked = isChecked
            binding.cbTerm4.isChecked = isChecked
        }
    }

    //필수부분들이 전부체크되야지만 위에가 파랗게하기
    private fun termAceppt(){
        binding.btnAccept.setOnClickListener{
            if (binding.cbTerm1.isChecked && binding.cbTerm2.isChecked && binding.cbTerm3.isChecked) {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                dismiss()
            }else {
                Toast.makeText(requireContext(), "사용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        )

        //회원가입 취소하게 만드는 코딩 삽입해야함
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}