package com.example.sweetrock

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.sweetrock.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var instance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.main.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_item_1 -> {
                    binding.main.viewPager.currentItem = 0
                }
                R.id.nav_item_2 -> {
                    binding.main.viewPager.currentItem = 1
                }
                R.id.nav_item_3 -> {
                    binding.main.viewPager.currentItem = 2
                }
                R.id.nav_item_4 -> {
                    binding.main.viewPager.currentItem = 3
                }
            }
            true
        }
        binding.main.viewPager.adapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.main.viewPager.isUserInputEnabled = false
        binding.main.viewPager.setPageTransformer(MarginPageTransformer(100))

        binding.main.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                if (!binding.toolbarTitle2.isVisible) binding.toolbarTitle2.isVisible = true

                binding.toolbarTitle.translationX = -positionOffset * binding.toolbar.width
                binding.toolbarTitle2.translationX = -positionOffset * binding.toolbar.width + binding.toolbar.width

                binding.toolbarTitle2.text = when (position) {
                    0 -> getString(R.string.about_title)
                    1 -> getString(R.string.reviews_title)
                    2 -> getString(R.string.account_title)
                    else -> getString(R.string.sweet_rock)
                }

                binding.toolbarTitle.text = when (position) {
                    0 -> getString(R.string.catalog_title)
                    1 -> getString(R.string.about_title)
                    2 -> getString(R.string.reviews_title)
                    3 -> getString(R.string.account_title)
                    else -> getString(R.string.sweet_rock)
                }

                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                when(state) {
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        if (binding.toolbarTitle2.isVisible) binding.toolbarTitle2.isVisible = false
                    }
                    else -> {}
                }
                super.onPageScrollStateChanged(state)
            }
        })

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}