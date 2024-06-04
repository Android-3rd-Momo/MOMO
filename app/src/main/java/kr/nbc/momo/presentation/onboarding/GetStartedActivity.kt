package kr.nbc.momo.presentation.onboarding
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityGetStartedBinding
import kr.nbc.momo.presentation.onboarding.onBoard.OnBoardingFragment

class GetStartedActivity : AppCompatActivity() {
    private val binding by lazy {ActivityGetStartedBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, OnBoardingFragment())
                .commit()
        }

    }

}