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
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentDevelopmentTypeBinding

class DevelopmentTypeFragment : Fragment() {
    private var _binding: FragmentDevelopmentTypeBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevelopmentTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveSelectedChips()
        initView()
    }

    private fun saveSelectedChips() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChipTexts = checkedIds.map { id ->
                group.findViewById<Chip>(id).text.toString()
            }
            onBoardingSharedViewModel.updateTypeOfDevelopment(selectedChipTexts)

            checkedIds.forEach { id ->
                val chip = group.findViewById<Chip>(id)
                if (chip.isChecked) {
                    onBoardingSharedViewModel.addSelectedTypeChipId(resources.getResourceEntryName(chip.id))
                } else {
                    onBoardingSharedViewModel.removeSelectedTypeChipId(resources.getResourceEntryName(chip.id))
                }
            }
        }

    }

    private fun initView(){
        val ssb = SpannableStringBuilder("개발 직군을 골라주세요!\n")
        val blue = Color.parseColor("#2D64CF")
        val black = Color.parseColor("#000000")
        ssb.setSpan(ForegroundColorSpan(blue), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(black), 5, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSubtitle.text = ssb
        val developType = resources.getStringArray(R.array.chipGroupDevelopmentOccupations)
        setChipGroup(developType, binding.chipGroup)
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
                setOnClickListener {
                    setChipBackgroundColorResource(R.color.blue)
                    setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
            }
            chipGroup.addView(chip)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}