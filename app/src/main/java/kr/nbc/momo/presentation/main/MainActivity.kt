package kr.nbc.momo.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.R
import kr.nbc.momo.databinding.ActivityMainBinding
import kr.nbc.momo.presentation.chattingroom.ChattingRoomFragment
import kr.nbc.momo.presentation.onboarding.GetStartedActivity
import kr.nbc.momo.presentation.signup.SignUpFragment

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
        supportFragmentManager.beginTransaction().apply {
//            add(R.id.flTest, ChattingRoomFragment())
            add(R.id.flTest, SignUpFragment())
            commit()
        }

        // SharedPreferences를 통해 앱이 처음 실행되었는지 확인
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // 처음 실행된 경우 GetStartedActivity로 이동
            startActivity(Intent(this, GetStartedActivity::class.java))
            finish()

            // 처음 실행됨을 기록
            with(sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }
        } else {
            setContentView(R.layout.activity_main)
        }
    }
}