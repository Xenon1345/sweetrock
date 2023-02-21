package com.example.sweetrock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sweetrock.databinding.FragmentCatalogBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class CatalogFragment : Fragment() {
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        val view = binding.root

        val catalog = loadCatalog()!!

        binding.catalogViewPager.adapter = CatalogViewPagerAdapter(catalog.toString(), childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.catalogViewPager) { tab, position ->
            tab.text = catalog.names()!!.getString(position)
        }.attach()
        return view
    }

    private fun loadCatalog(): JSONObject? {
        val json: String? = try {
            val input: InputStream = requireActivity().assets.open(getString(R.string.json_catalog_file))
            val size: Int = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json?.let { JSONObject(it) }
    }
}