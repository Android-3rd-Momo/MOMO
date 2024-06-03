package kr.nbc.momo.presentation.onboarding.term

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.databinding.FragmentTermBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TermFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TermFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTermBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTermBinding.inflate(inflater, container, false)
        return binding.root

        binding.btnAccept.setOnClickListener{
            dismiss()
        }
    }


}