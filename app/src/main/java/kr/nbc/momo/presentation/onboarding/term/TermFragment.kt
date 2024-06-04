package kr.nbc.momo.presentation.onboarding.term

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentLoginBinding
import kr.nbc.momo.databinding.FragmentTermBinding

class TermFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTermBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TermViewModel by viewModels()
    private lateinit var termAdapter: TermRecyclerViewAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTermBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.terms.observe(viewLifecycleOwner) { terms ->
            termAdapter = TermRecyclerViewAdapter(terms)
            binding.recyclerView.adapter = termAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
        }

        binding.btnAccept.setOnClickListener {
            dismiss()
        }

    }

    private fun buttonClickListner() {

        listOf(binding.ivCheckboxAllAccept, binding.tvAllAccept, binding.tvAllAcceptDesc)
    }

}