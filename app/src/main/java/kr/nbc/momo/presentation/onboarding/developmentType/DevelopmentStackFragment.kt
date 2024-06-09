package kr.nbc.momo.presentation.onboarding.developmentType

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentDevelopmentStackBinding
import kr.nbc.momo.presentation.main.MainActivity
@AndroidEntryPoint
class DevelopmentStackFragment : Fragment() {
    private var _binding: FragmentDevelopmentStackBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevelopmentStackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        getDevelopmentStack()
    }

    private fun setOnClickListeners() {
        binding.btnPrevious.setOnClickListener {
            (activity as DevelopmentActivity).binding.viewPager.currentItem -= 1
        }
        binding.btnNext.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            saveDevelopmentStack()
            onBoardingSharedViewModel.saveUserProfile()
        }
        binding.tvSkip.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            onBoardingSharedViewModel.clearChipData()
        }
    }

    private fun saveDevelopmentStack() {
        val stack = binding.etStack.text.toString()
        onBoardingSharedViewModel.updateStackOfDevelopment(stack)
    }

    private fun getDevelopmentStack() {
        lifecycleScope.launchWhenStarted {
            onBoardingSharedViewModel.stackOfDevelopment.collect { stack ->
                if (stack.isNotEmpty()) {
                    binding.etStack.setText(stack)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}