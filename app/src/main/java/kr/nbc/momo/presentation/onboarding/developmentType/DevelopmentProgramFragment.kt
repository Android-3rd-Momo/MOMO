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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.databinding.FragmentDevelopmentProgramBinding

@AndroidEntryPoint
class DevelopmentProgramFragment : Fragment() {
    private var _binding: FragmentDevelopmentProgramBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevelopmentProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        saveDevelopmentOfProgramList()
    }


    private fun saveDevelopmentOfProgramList() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChips = checkedIds.map { id ->
                group.findViewById<Chip>(id).text.toString()
            }
            onBoardingSharedViewModel.updateProgramOfDevelopment(selectedChips)

            checkedIds.forEach { id ->
                val chip = group.findViewById<Chip>(id)
                if (chip.isChecked) {
                    onBoardingSharedViewModel.addSelectedProgramChipId(resources.getResourceEntryName(chip.id))
                } else {
                    onBoardingSharedViewModel.removeSelectedProgramChipId(resources.getResourceEntryName(chip.id))
                }
            }
        }
    }

    private fun initView(){
        val ssb = SpannableStringBuilder("사용하시는 프로그램을 골라주세요!\n")
        val blue = Color.parseColor("#2D64CF")
        val black = Color.parseColor("#000000")
        ssb.setSpan(ForegroundColorSpan(black), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(blue), 6, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(StyleSpan(Typeface.BOLD), 6, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(black), 10, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSubtitle.text = ssb
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}