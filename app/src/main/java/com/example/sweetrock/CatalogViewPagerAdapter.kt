package com.example.sweetrock

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.json.JSONObject

class CatalogViewPagerAdapter(catalogStr: String, fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    private val catalog = JSONObject(catalogStr)
    private val numTabs = catalog.length()
    override fun getItemCount() = numTabs

    override fun createFragment(position: Int): Fragment {
        val names = catalog.names()
        val name = names!!.getString(position)
        val data = catalog.getJSONObject(name)
        val fragment = CatalogCategoryFragment()
        val args = Bundle()
        args.putString(MainActivity.instance.getString(R.string.key_category_data), data.toString())
        fragment.arguments = args
        return fragment
    }
}