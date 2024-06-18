package kr.nbc.momo.presentation.onboarding.developmentType

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentDevelopmentStackBinding
import kr.nbc.momo.util.addTextWatcherWithError

@AndroidEntryPoint
class DevelopmentStackFragment : Fragment() {
    private var _binding: FragmentDevelopmentStackBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by activityViewModels()
    private lateinit var btnConfirm: Button

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
        initView()
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

    private fun initView() {
        btnConfirm = requireActivity().findViewById<Button>(R.id.btnConfirm)
        val ssb = SpannableStringBuilder(
            getString(R.string.please_introduce_your_stack)
        )
        val blue = Color.parseColor("#2D64CF")
        val black = Color.parseColor("#000000")
        ssb.setSpan(ForegroundColorSpan(blue), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(black), 5, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSubtitle.text = ssb


        binding.etStack.addTextWatcherWithError(500, "기술스택", btnConfirm, binding.tvTextCount)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}