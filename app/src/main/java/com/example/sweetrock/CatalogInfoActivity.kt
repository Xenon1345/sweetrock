package com.example.sweetrock

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.sweetrock.databinding.ActivityCatalogInfoBinding
import com.squareup.picasso.Picasso

class CatalogInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCatalogInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCatalogInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = intent.getStringExtra(getString(R.string.key_name))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        Picasso.get().load(intent.getStringExtra(getString(R.string.key_image))).placeholder(R.drawable.no_image).into(binding.catalogInfo.imageView)
        binding.catalogInfo.nameTextView.text = intent.getStringExtra(getString(R.string.key_name))
        binding.catalogInfo.priceTextView.text = String.format(getString(R.string.price_format), intent.getDoubleExtra(getString(R.string.key_price), -1.0))
        binding.catalogInfo.descriptionTextView.text = intent.getStringExtra(getString(R.string.key_description))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}