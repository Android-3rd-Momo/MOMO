package kr.nbc.momo.presentation.onboarding.term

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentTermBinding
import kr.nbc.momo.presentation.onboarding.signup.SignUpFragment

@AndroidEntryPoint
class TermFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTermBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTermBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allTermCheck()
        termAccept()
        intentTermDesc()

        //선택약관 동의시에 카카오톡 알람을 주게 하는 메소드 만들기
    }

    private fun allTermCheck() {
        binding.cbAllAccept.setOnCheckedChangeListener { _, isChecked ->
            setAllCheckbox(isChecked)
        }

        binding.cbTerm1.setOnCheckedChangeListener { _, _ -> updateAllAcceptCheckbox() }
        binding.cbTerm2.setOnCheckedChangeListener { _, _ -> updateAllAcceptCheckbox() }
        binding.cbTerm3.setOnCheckedChangeListener { _, _ -> updateAllAcceptCheckbox() }
        binding.cbTerm4.setOnCheckedChangeListener { _, _ -> updateAllAcceptCheckbox() }
        binding.cbTerm5.setOnCheckedChangeListener { _, _ -> updateAllAcceptCheckbox() }
    }

    private fun setAllCheckbox(isChecked: Boolean) {
        binding.cbTerm1.isChecked = isChecked
        binding.cbTerm2.isChecked = isChecked
        binding.cbTerm3.isChecked = isChecked
        binding.cbTerm4.isChecked = isChecked
        binding.cbTerm5.isChecked = isChecked
    }

    private fun updateAllAcceptCheckbox() {
        binding.cbAllAccept.setOnCheckedChangeListener(null)
        binding.cbAllAccept.isChecked =
            binding.cbTerm1.isChecked && binding.cbTerm2.isChecked && binding.cbTerm3.isChecked && binding.cbTerm4.isChecked && binding.cbTerm5.isChecked
        binding.cbAllAccept.setOnCheckedChangeListener { _, isChecked ->
            setAllCheckbox(isChecked)
        }
    }

    //필수부분들이 전부체크되야지만 위에가 파랗게하기
    private fun termAccept() {
        binding.btnAccept.setOnClickListener {
            if (binding.cbTerm1.isChecked && binding.cbTerm2.isChecked && binding.cbTerm3.isChecked && binding.cbTerm4.isChecked) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, SignUpFragment())
                    .addToBackStack(null)
                    .commit()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "사용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun intentTermDesc() {
        binding.tvAccept2Desc.setOnClickListener {
            val fragmentPrivateTerm = PrivateTermFragment()
            fragmentPrivateTerm.setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            fragmentPrivateTerm.show(parentFragmentManager, fragmentPrivateTerm.tag)
        }
        binding.tvAccept3Desc.setOnClickListener {
            val fragmentServiceTerm = ServiceTermFragment()
            fragmentServiceTerm.setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            fragmentServiceTerm.show(parentFragmentManager, fragmentServiceTerm.tag)
        }
        binding.tvAccept4Desc.setOnClickListener {
            val fragmentCommunityTerm = CommunityTermFragment()
            fragmentCommunityTerm.setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            fragmentCommunityTerm.show(parentFragmentManager, fragmentCommunityTerm.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}