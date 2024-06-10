package kr.nbc.momo.presentation.onboarding.developmentType

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityDevelopmentBinding

@AndroidEntryPoint
class DevelopmentActivity : AppCompatActivity() {

    lateinit var binding: ActivityDevelopmentBinding
    private lateinit var viewPagerAdapter: DevelopmentViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDevelopmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = DevelopmentViewPagerAdapter(this)
        viewPagerAdapter.addFragment(DevelopmentTypeFragment())
        viewPagerAdapter.addFragment(DevelopmentProgramFragment())
        viewPagerAdapter.addFragment(DevelopmentStackFragment())

        binding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
        }
    }
}