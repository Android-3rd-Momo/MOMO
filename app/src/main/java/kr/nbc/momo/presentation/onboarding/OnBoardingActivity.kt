package kr.nbc.momo.presentation.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityOnBoardingBinding
import kr.nbc.momo.presentation.onboarding.onBoard.OnBoardingFragment


@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityOnBoardingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, OnBoardingFragment())
            .commit()
    }

}