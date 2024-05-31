package kr.nbc.momo.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityGetStartedBinding
import kr.nbc.momo.presentation.main.MainActivity
import me.relex.circleindicator.CircleIndicator3

class GetStartedActivity : AppCompatActivity() {

    private var titleList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    private val binding by lazy { ActivityGetStartedBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        postToList()

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        viewPager2.adapter = GetStartedViewPagerAdapter(titleList, descList, imageList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(viewPager2)

        binding.buttonWithoutLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun addToList(title: String, description: String, image:Int) {
        titleList.add(title)
        descList.add(description)
        imageList.add(image)
    }

    private fun postToList(){
        addToList("안녕하세요", "첫번째입니다", R.drawable.onboarding_illust1)
        addToList("안녕하세요", "두번째입니다", R.drawable.onboardingillust2)
        addToList("안녕하세요", "세번째입니다", R.drawable.onboardingillust3)
    }

}