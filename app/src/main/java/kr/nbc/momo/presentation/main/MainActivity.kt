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
import kr.nbc.momo.presentation.group.read.ReadGroupFragment
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
//            add(R.id.flTest, ReadGroupFragment())
//            add(R.id.flTest, CreateGroupFragment())
//            add(R.id.flTest, ChattingRoomFragment())
            add(R.id.flTest, SignUpFragment())

            commit()
        }

    }
}