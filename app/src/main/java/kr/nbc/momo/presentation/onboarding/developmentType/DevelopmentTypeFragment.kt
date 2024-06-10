package kr.nbc.momo.presentation.onboarding.developmentType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}