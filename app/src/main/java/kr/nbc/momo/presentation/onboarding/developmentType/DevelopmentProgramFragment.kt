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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentDevelopmentProgramBinding

@AndroidEntryPoint
class DevelopmentProgramFragment : Fragment() {
    private var _binding: FragmentDevelopmentProgramBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevelopmentProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        val ssb = SpannableStringBuilder(getString(R.string.please_pick_your_dev_lang))
        val blue = Color.parseColor("#2D64CF")
        val black = Color.parseColor("#000000")
        ssb.setSpan(ForegroundColorSpan(black), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(blue), 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(StyleSpan(Typeface.BOLD), 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(black), 10, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSubtitle.text = ssb
        val developLanguage = resources.getStringArray(R.array.chipProgramingLanguage)
        setChipGroup(developLanguage, binding.chipGroup)
    }

    private fun setChipGroup(chipList: Array<String>, chipGroup: ChipGroup) {
        for (chipText in chipList) {
            val chip = Chip(requireContext()).apply {
                text = chipText
                setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.tv_chip_state_color
                    )
                )
                setChipBackgroundColorResource(R.color.bg_chip_state_color)
                isCheckable = true
                setOnClickListener {
                    if (isChecked) {
                        onBoardingSharedViewModel.addSelectedProgramChipId(chipText)
                    }else {
                        onBoardingSharedViewModel.removeSelectedProgramChipId(chipText)
                    }
                }
            }
            chipGroup.addView(chip)
        }
    }
}