package com.example.sweetrock

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    private val numTabs = 4
    override fun getItemCount() = numTabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CatalogFragment()
            1 -> AboutFragment()
            2 -> ReviewsFragment()
            else -> AccountFragment()
        }
    }
}