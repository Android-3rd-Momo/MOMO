package kr.nbc.momo.presentation.onboarding.developmentType

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.FragmentDevelopmentProgramBinding
import kr.nbc.momo.presentation.main.MainActivity

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
        setOnClickListeners()
        saveDevelopmentOfProgramList()
    }

    private fun setOnClickListeners() {
        binding.btnPrevious.setOnClickListener {
            val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            viewPager.currentItem -= 1
        }
        binding.btnNext.setOnClickListener {
            val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            viewPager.currentItem += 1        }
        binding.tvSkip.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            onBoardingSharedViewModel.clearChipData()
        }
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

    private fun observeDevelopmentOfProgramList() {
        lifecycleScope.launchWhenStarted {
            onBoardingSharedViewModel.programOfDevelopment.collect { programs ->
                for (i in 0 until binding.chipGroup.childCount) {
                    val chip = binding.chipGroup.getChildAt(i) as Chip
                    chip.isChecked = programs.contains(chip.text.toString())
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}