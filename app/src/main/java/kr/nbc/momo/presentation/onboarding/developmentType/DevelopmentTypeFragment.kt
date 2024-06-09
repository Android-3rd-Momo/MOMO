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
        saveSelectedChips()
    }

    private fun setOnClickListeners() {
        binding.btnNext.setOnClickListener {
            (activity as DevelopmentActivity).binding.viewPager.currentItem += 1
        }

        binding.tvSkip.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            onBoardingSharedViewModel.clearChipData()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}