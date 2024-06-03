package kr.nbc.momo.presentation.chatting.chattinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kr.nbc.momo.databinding.FragmentChattingListBinding

@AndroidEntryPoint
class ChattingListFragment : Fragment() {
    private var _binding: FragmentChattingListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}