package kr.nbc.momo.presentation.onboarding.developmentType

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.databinding.FragmentDevelopmentBinding
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class DevelopmentFragment : Fragment() {
    private var _binding: FragmentDevelopmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: DevelopmentViewPagerAdapter
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevelopmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        setOnClickListner()
    }

    private fun initViewPager() {
        viewPagerAdapter = DevelopmentViewPagerAdapter(this)
        viewPagerAdapter.addFragment(DevelopmentTypeFragment())
        viewPagerAdapter.addFragment(DevelopmentProgramFragment())
        viewPagerAdapter.addFragment(DevelopmentStackFragment())
    }

    private fun setOnClickListner() {
        with(binding) {
            tvSkip.setOnClickListener {
                onBoardingSharedViewModel.clearTemporaryData()
                //val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            viewPager.apply {
                adapter = viewPagerAdapter
                isUserInputEnabled = false
            }

            btnPrevious.apply {
                setVisibleToGone()
                setOnClickListener {
                    if (viewPager.currentItem == 1) {
                        setVisibleToGone()
                    } else if (viewPager.currentItem == 2) {
                        btnConfirm.setVisibleToGone()
                        btnNext.setVisibleToVisible()
                    }
                    viewPager.currentItem--
                }
            }

            btnConfirm.apply {
                setVisibleToGone()
                setOnClickListener {
                    //val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }

            btnNext.apply {
                setOnClickListener {
                    if (viewPager.currentItem == 0) {
                        btnPrevious.setVisibleToVisible()
                    } else if (viewPager.currentItem == 1) {
                        setVisibleToGone()
                        btnConfirm.setVisibleToVisible()
                    }
                    viewPager.currentItem++
                }
            }
        }


    }
}