package kr.nbc.momo.presentation.onboarding.developmentType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.databinding.FragmentDevelopmentStackBinding

@AndroidEntryPoint
class DevelopmentStackFragment : Fragment() {
    private var _binding: FragmentDevelopmentStackBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevelopmentStackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDevelopmentStack()
    }

    override fun onStop() {
        super.onStop()
        saveDevelopmentStack()
        onBoardingSharedViewModel.saveUserProfile()
    }

    private fun saveDevelopmentStack() {
        val stack = binding.etStack.text.toString()
        onBoardingSharedViewModel.updateStackOfDevelopment(stack)
    }

    private fun getDevelopmentStack() {
        lifecycleScope.launch {
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