package kr.nbc.momo.presentation.onboarding.developmentType

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.databinding.ActivityDevelopmentBinding
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToVisible

@AndroidEntryPoint
class DevelopmentActivity : AppCompatActivity() {

    lateinit var binding: ActivityDevelopmentBinding
    private lateinit var viewPagerAdapter: DevelopmentViewPagerAdapter
    private val onBoardingSharedViewModel: OnBoardingSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDevelopmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = DevelopmentViewPagerAdapter(this)
        viewPagerAdapter.addFragment(DevelopmentTypeFragment())
        viewPagerAdapter.addFragment(DevelopmentProgramFragment())
        viewPagerAdapter.addFragment(DevelopmentStackFragment())

        with(binding){
            tvSkip.setOnClickListener {
                onBoardingSharedViewModel.clearTemporaryData()
                val intent = Intent(this@DevelopmentActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            viewPager.apply {
                adapter = viewPagerAdapter
                isUserInputEnabled = false
            }

            btnPrevious.apply {
                setVisibleToGone()
                setOnClickListener {
                    if (viewPager.currentItem == 1){
                        setVisibleToGone()
                    }else if(viewPager.currentItem == 2){
                        btnConfirm.setVisibleToGone()
                        btnNext.setVisibleToVisible()
                    }
                    viewPager.currentItem --
                }
            }

            btnConfirm.apply {
                setVisibleToGone()
                setOnClickListener {

                    val intent = Intent(this@DevelopmentActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }

            btnNext.apply {
                setOnClickListener {
                    if (viewPager.currentItem == 0){
                        btnPrevious.setVisibleToVisible()
                    }else if (viewPager.currentItem == 1){
                        setVisibleToGone()
                        btnConfirm.setVisibleToVisible()
                    }
                    viewPager.currentItem ++
                }
            }
        }
    }
}