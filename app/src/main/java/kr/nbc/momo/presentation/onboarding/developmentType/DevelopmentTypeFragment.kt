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
        saveSelectedChips()
    }

    private fun setOnClickListeners() {
        binding.btnNext.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, DevelopmentProgramFragment())
                .commit()
        }

        binding.tvSkip.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }

    private fun saveSelectedChips() {
        val selectedChipTexts = mutableListOf<String>()
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                val chipId = "chip_${i+1}"
                onBoardingSharedViewModel.addSelectedTypeChipId(chipId)
                selectedChipTexts.add(chip.text.toString())
            } else {
                onBoardingSharedViewModel.removeSelectedTypeChipId("chip_${i+1}")
            }
        }
        onBoardingSharedViewModel.updateTypeOfDevelopment(selectedChipTexts)
    }

    private fun observeSelectedChips() {
        lifecycleScope.launchWhenStarted {
            onBoardingSharedViewModel.selectedTypeChipIds.collect { selectedChipIds ->
                for (i in 0 until binding.chipGroup.childCount) {
                    val chip = binding.chipGroup.getChildAt(i) as Chip
                    val chipId = resources.getResourceEntryName(chip.id)
                    chip.isChecked = selectedChipIds.contains(chipId)
                }
            }
        }
    }

    private fun getSelectedChips() {
        val selectedChipIds = onBoardingSharedViewModel.selectedTypeChipIds.value
        for (chipId in selectedChipIds) {
            val chip = binding.chipGroup.findViewById<Chip>(resources.getIdentifier(chipId, "id", requireContext().packageName))
            chip.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}