package kr.nbc.momo.presentation.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityMainBinding
import kr.nbc.momo.presentation.chatting.chattinglist.ChattingListFragment
import kr.nbc.momo.presentation.chatting.chattingroom.ChattingRoomFragment
import kr.nbc.momo.presentation.home.HomeFragment
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.presentation.signup.SignUpFragment
import kr.nbc.momo.presentation.mypage.MyPageFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initFirstFragment()
        setUpBottomNavigation()
        onBoardingLaunch()

        val intent = Intent(this, GetStartedActivity::class.java)
        startActivity(intent)
    }
    private fun initFirstFragment() {
        //메인화면은 SearchFragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, HomeFragment())
            commit()
        }
    }


    private fun setUpBottomNavigation() {
        binding.navigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit()
                    true
                }

                R.id.chattingRoomFragment -> {
                    val chattingRoomFragment = ChattingRoomFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, chattingRoomFragment)
                        .commit()
                    true
                }

                R.id.signUpFragment -> {
                    val signUpFragment = SignUpFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, signUpFragment)
                        .commit()
                    true
                }

                else -> false
            }
        }
        binding.navigationView.setOnItemReselectedListener {
            when (it.itemId){
                R.id.homeFragment -> {}
                R.id.chattingRoomFragment -> {}
                R.id.myPageFragment -> {}
            }
        }
    }

    private fun onBoardingLaunch(){
        try {
            val sharedPreferences: SharedPreferences = getSharedPreferences("onBoarding", MODE_PRIVATE)
            val isFirstLaunch = sharedPreferences.getBoolean("firstLaunch", true)

            if (isFirstLaunch) {
                val editor = sharedPreferences.edit()
                editor.putBoolean("firstLaunch", false)
                editor.apply()

                val intent = Intent(this, GetStartedActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                setContentView(binding.root)
            }
        } catch (e: Exception) {
            e.printStackTrace()        }
    }
}