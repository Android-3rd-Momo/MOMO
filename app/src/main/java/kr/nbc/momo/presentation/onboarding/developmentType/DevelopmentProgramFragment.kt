package kr.nbc.momo.presentation.onboarding.developmentType

import android.os.Bundle
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}