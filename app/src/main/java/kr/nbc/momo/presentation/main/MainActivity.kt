package kr.nbc.momo.presentation.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityMainBinding
import kr.nbc.momo.presentation.onboarding.OnBoardingActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBoardingLaunch()
        setUpNavigation()
    }

    private fun setUpNavigation(){
        val hostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = hostFragment.navController
        NavigationUI.setupWithNavController(binding.navigationView, navController)
    }


    private fun onBoardingLaunch(){
        try {
            val sharedPreferences: SharedPreferences = getSharedPreferences("onBoarding", MODE_PRIVATE)
            val isFirstLaunch = sharedPreferences.getBoolean("firstLaunch", true)

            if (isFirstLaunch) {
                val editor = sharedPreferences.edit()
                editor.putBoolean("firstLaunch", false)
                editor.apply()

                val intent = Intent(this, OnBoardingActivity::class.java)
                startActivity(intent)
            } else {
                setContentView(binding.root)
            }
        } catch (e: Exception) {
            e.printStackTrace()        }
    }
}