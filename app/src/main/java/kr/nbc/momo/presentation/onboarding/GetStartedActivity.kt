package kr.nbc.momo.presentation.onboarding
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityGetStartedBinding
import kr.nbc.momo.presentation.onboarding.onBoard.OnBoardingFragment


@AndroidEntryPoint
class GetStartedActivity : AppCompatActivity() {
    private val binding by lazy {ActivityGetStartedBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, OnBoardingFragment())
                .commit()
        }


    }

}