package kr.nbc.momo.presentation.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityMainBinding
import kr.nbc.momo.presentation.chattingroom.ChattingRoomFragment
import kr.nbc.momo.presentation.group.create.CreateGroupFragment
import kr.nbc.momo.presentation.home.main.HomeFragment

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


        supportFragmentManager.beginTransaction().replace(R.id.flTest, HomeFragment()).commit()
        binding.navigationView.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.homeIcon -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val homeFragment = HomeFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.flTest, homeFragment).commit()
                    }

                    R.id.chatIcon -> {
                        val chattingRoomFragment = ChattingRoomFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.flTest, chattingRoomFragment).commit()
                    }

                    R.id.myPageIcon -> {
                        val createGroupFragment = CreateGroupFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.flTest, createGroupFragment).commit()
                    }
                }
                true
            }
        }
    }
}