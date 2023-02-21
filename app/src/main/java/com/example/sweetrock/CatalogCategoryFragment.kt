package com.example.sweetrock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sweetrock.databinding.FragmentCatalogCategoryBinding
import org.json.JSONObject

class CatalogCategoryFragment : Fragment() {
    private var _binding: FragmentCatalogCategoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogCategoryBinding.inflate(inflater, container, false)
        val view = binding.root
        val categoryData = JSONObject(requireArguments().getString(getString(R.string.key_category_data), "{}"))

        binding.catalogCategoryRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.catalogCategoryRecyclerView.adapter = CatalogCategoryRecyclerAdapter(categoryData)

        return view
    }
}