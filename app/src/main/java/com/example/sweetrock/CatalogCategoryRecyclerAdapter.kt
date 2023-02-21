package com.example.sweetrock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class CatalogCategoryRecyclerAdapter(private val categoryData: JSONObject) : RecyclerView.Adapter<CatalogCategoryRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_catalog_subcategory, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val names = categoryData.names()
        val name = names!!.getString(position)
        val data = categoryData.getJSONObject(name)
        holder.bind(name, data)
    }

    override fun getItemCount() = categoryData.names()!!.length()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nameTextView: TextView
        private var recyclerView: RecyclerView

        init {
            nameTextView = view.findViewById(R.id.catalog_subcategory_text_view)
            recyclerView = view.findViewById(R.id.catalog_subcategory_recycler_view)
            val layoutManager = GridLayoutManager(recyclerView.context, 2)
            recyclerView.layoutManager = layoutManager
        }

        fun bind(subcategoryName: String, subcategoryData: JSONObject) {
            nameTextView.text = subcategoryName
            recyclerView.adapter = CatalogSubcategoryRecyclerAdapter(subcategoryData)
        }
    }
}