package kr.nbc.momo.presentation.onboarding.developmentType

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
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

@AndroidEntryPoint
class DevelopmentStackFragment : Fragment() {
    private var _binding: FragmentDevelopmentStackBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by activityViewModels()
    private lateinit var btnConfirm : Button

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
            "기술스택에 대해 알려주세요\n"
        )
        val blue = Color.parseColor("#2D64CF")
        val black = Color.parseColor("#000000")
        ssb.setSpan(ForegroundColorSpan(blue), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(black), 5, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSubtitle.text = ssb


        binding.etStack.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = binding.etStack.text.length
                val lengthText = "$textLength/500자"
                binding.tvTextCount.text = lengthText
                if (textLength > 500) {
                    binding.etStack.error = "500자를 초과했습니다!"
                    btnConfirm.isEnabled = false
                } else {
                    binding.etStack.error = null
                    btnConfirm.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = binding.etStack.text.length.toString()
                val lengthText = "$textLength/500자"
                binding.tvTextCount.text = lengthText
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}