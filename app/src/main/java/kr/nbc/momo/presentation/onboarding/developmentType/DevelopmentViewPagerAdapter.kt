package kr.nbc.momo.presentation.onboarding.developmentType

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DevelopmentViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentList: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}

