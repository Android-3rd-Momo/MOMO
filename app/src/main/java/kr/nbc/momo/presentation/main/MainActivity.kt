package kr.nbc.momo.presentation.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityMainBinding
import kr.nbc.momo.presentation.chatting.chattinglist.ChattingListFragment
import kr.nbc.momo.presentation.group.read.ReadGroupFragment
import kr.nbc.momo.presentation.home.HomeFragment
import kr.nbc.momo.presentation.mypage.RootFragment
import kr.nbc.momo.presentation.onboarding.OnBoardingActivity
import kr.nbc.momo.presentation.userinfo.UserInfoFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //initFirstFragment()
        //setUpBottomNavigation()
        onBoardingLaunch()
        setUpNavigation()
    }

    private fun setUpNavigation(){
        val hostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = hostFragment.navController
        NavigationUI.setupWithNavController(binding.navigationView, navController)
    }


    private fun initFirstFragment() {
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

                R.id.chattingListFragment -> {
                    val chattingListFragment = ChattingListFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, chattingListFragment)
                        .commit()
                    true

                }

                R.id.rootFragment -> {
                    val rootFragment = RootFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, rootFragment)
                        .commit()
                    true
                }

                else -> false
            }
        }

        binding.navigationView.setOnItemReselectedListener {
            when (it.itemId){
                R.id.homeFragment -> {}
                R.id.chattingListFragment -> {}
                R.id.rootFragment -> {}
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

                val intent = Intent(this, OnBoardingActivity::class.java)
                startActivity(intent)
            } else {
                setContentView(binding.root)
            }
        } catch (e: Exception) {
            e.printStackTrace()        }
    }

    fun selectNavigationItem(itemId: Int) {
        binding.navigationView.selectedItemId = itemId
    }

    fun beginTransactionRead() {
        val readGroupFragment = ReadGroupFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, readGroupFragment)
            .addToBackStack("Read")
            .commit()
    }

    fun beginTransactionUserInfo() {
        val userInfoFragment = UserInfoFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, userInfoFragment)
            .addToBackStack(null)
            .commit()
    }
}