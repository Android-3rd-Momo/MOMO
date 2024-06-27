package kr.nbc.momo.presentation.onboarding.term

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentTermBinding
import kr.nbc.momo.presentation.onboarding.signup.SignUpFragment
import kr.nbc.momo.util.makeToastWithStringRes

@AndroidEntryPoint
class TermFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTermBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListner()
        allTermCheck()
    }

    private fun allTermCheck() {
        with(binding) {
            cbAllAccept.setOnCheckedChangeListener { _, isChecked ->
                setAllCheckbox(isChecked)
            }

            listOf(cbTerm1, cbTerm2, cbTerm3, cbTerm4).forEach { checkbox ->
                checkbox.setOnCheckedChangeListener { _, _ -> updateAllAcceptCheckbox() }
            }
        }
    }

    private fun setAllCheckbox(isChecked: Boolean) {
        with(binding) {
            listOf(cbTerm1, cbTerm2, cbTerm3, cbTerm4).forEach { it.isChecked = isChecked }
        }
    }

    private fun updateAllAcceptCheckbox() {
        with(binding) {
            cbAllAccept.setOnCheckedChangeListener(null)
            cbAllAccept.isChecked = listOf(cbTerm1, cbTerm2, cbTerm3, cbTerm4).all { it.isChecked }
            btnAccept.isEnabled = listOf(cbTerm1, cbTerm2, cbTerm3, cbTerm4).all { it.isChecked }
            cbAllAccept.setOnCheckedChangeListener { _, isChecked ->
                setAllCheckbox(isChecked)
            }
        }
    }

    private fun setOnClickListner() {
        with(binding) {
            val termsChecked = listOf(cbTerm1, cbTerm2, cbTerm3, cbTerm4)
            val allTermsChecked = listOf(cbTerm1, cbTerm2, cbTerm3, cbTerm4)

            cbAllAccept.setOnCheckedChangeListener(null)
            cbAllAccept.isChecked = allTermsChecked.all { it.isChecked }
            cbAllAccept.setOnCheckedChangeListener { _, isChecked ->
                setAllCheckbox(isChecked)
            }

            tvAccept2Desc.setOnClickListener {
                val fragmentPrivateTerm = PrivateTermFragment()
                fragmentPrivateTerm.setStyle(
                    STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
                fragmentPrivateTerm.show(parentFragmentManager, fragmentPrivateTerm.tag)
            }

            tvAccept3Desc.setOnClickListener {
                val fragmentServiceTerm = ServiceTermFragment()
                fragmentServiceTerm.setStyle(
                    STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
                fragmentServiceTerm.show(parentFragmentManager, fragmentServiceTerm.tag)
            }

            tvAccept4Desc.setOnClickListener {
                val fragmentCommunityTerm = CommunityTermFragment()
                fragmentCommunityTerm.setStyle(
                    STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
                fragmentCommunityTerm.show(parentFragmentManager, fragmentCommunityTerm.tag)
            }

            btnAccept.setOnClickListener {
                if (termsChecked.all { it.isChecked }) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, SignUpFragment())
                        .addToBackStack(null)
                        .commit()
                    dismiss()
                } else {
                    makeToastWithStringRes(requireContext(), R.string.term_title)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}