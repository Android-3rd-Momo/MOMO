package kr.nbc.momo.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityGetStartedBinding
import kr.nbc.momo.presentation.home.HomeFragment
import kr.nbc.momo.presentation.main.MainActivity
import kr.nbc.momo.presentation.onboarding.login.LoginFragment
import me.relex.circleindicator.CircleIndicator3

class GetStartedActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGetStartedBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initFirstFragment()

    }

    private fun initFirstFragment() {
        //메인화면은 SearchFragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, HomeFragment())
            commit()
        }
    }

}