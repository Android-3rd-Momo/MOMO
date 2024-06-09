package kr.nbc.momo.presentation.onboarding.developmentType

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentDevelopmentStackBinding
import kr.nbc.momo.databinding.FragmentDevelopmentTypeBinding
import kr.nbc.momo.presentation.main.MainActivity
@AndroidEntryPoint
class DevelopmentTypeFragment : Fragment() {
    private var _binding: FragmentDevelopmentTypeBinding? = null
    private val binding get() = _binding!!
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by viewModels()

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
        setOnClickListeners()
        observeSelectedChips()
        getSelectedChips()
    }

    private fun setOnClickListeners() {
        // Navigate to the next fragment when the next button is clicked
        binding.btnNext.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, DevelopmentProgramFragment())
                .addToBackStack(null)
                .commit()
        }

        // Skip to the main activity when the skip text is clicked
        binding.tvSkip.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }

        // Save selected chips when any chip is clicked
        binding.chipGroup.setOnCheckedChangeListener { _, _ -> saveSelectedChips() }
    }

    private fun saveSelectedChips() {
        val selectedChipTexts = mutableListOf<String>()
        val selectedChipIds = mutableListOf<String>()

        // Iterate over each chip and update the selected state in the ViewModel
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            val chipId = "chip_${i + 1}"

            if (chip.isChecked) {
                selectedChipTexts.add(chip.text.toString())
                selectedChipIds.add(chipId)
            }
        }

        // Update the ViewModel with selected chips
        onBoardingSharedViewModel.updateTypeOfDevelopment(selectedChipTexts)
        onBoardingSharedViewModel.updateSelectedTypeChipIds(selectedChipIds)
    }

    private fun observeSelectedChips() {
        lifecycleScope.launchWhenStarted {
            // Observe the selected chip IDs from the ViewModel and update the UI
            onBoardingSharedViewModel.selectedTypeChipIds.collect { selectedChipIds ->
                for (i in 0 until binding.chipGroup.childCount) {
                    val chip = binding.chipGroup.getChildAt(i) as Chip
                    val chipId = "chip_${i + 1}"
                    chip.isChecked = selectedChipIds.contains(chipId)
                }
            }
        }
    }

    private fun getSelectedChips() {
        // Get the selected chip IDs from the ViewModel and update the chip group
        val selectedChipIds = onBoardingSharedViewModel.selectedTypeChipIds.value
        for (chipId in selectedChipIds) {
            val chip = binding.chipGroup.findViewById<Chip>(
                resources.getIdentifier(chipId, "id", requireContext().packageName)
            )
            chip?.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}