package com.example.sweetrock

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONObject

class CatalogSubcategoryRecyclerAdapter(private val subcategoryData: JSONObject) : RecyclerView.Adapter<CatalogSubcategoryRecyclerAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.card_catalog_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val names = subcategoryData.names()
        val name = names!!.getString(position)
        val data = subcategoryData.getJSONObject(name)
        val imageURL = if (data.has(context.resources.getString(R.string.key_image))) data.getString(context.resources.getString(R.string.key_image)) else "https://media.discordapp.net/attachments/1006932921279590493/1073836774729711626/293.jpg?width=478&height=473"
        val price = data.getDouble(context.resources.getString(R.string.key_price))
        val desc = data.getString(context.resources.getString(R.string.key_description))
        holder.bind(name, imageURL, price, desc)
    }

    override fun getItemCount() = subcategoryData.names()!!.length()

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private var imageView: ImageView = view.findViewById(R.id.catalog_item_image_view)
        private var nameTextView: TextView = view.findViewById(R.id.catalog_item_name_text_view)
        private var priceTextView: TextView = view.findViewById(R.id.catalog_item_price_text_view)

        fun bind(itemName: String, imageURL: String, itemPrice: Double, itemDesc: String) {
            Picasso.get().load(imageURL).placeholder(R.drawable.no_image).into(imageView)
            nameTextView.text = itemName
            priceTextView.text = String.format(view.resources.getString(R.string.price_format), itemPrice)
            view.setOnClickListener {
                val intent = Intent(view.context, CatalogInfoActivity::class.java)
                intent.putExtra(view.resources.getString(R.string.key_name), itemName)
                intent.putExtra(view.resources.getString(R.string.key_image), imageURL)
                intent.putExtra(view.resources.getString(R.string.key_price), itemPrice)
                intent.putExtra(view.resources.getString(R.string.key_description), itemDesc)
                view.context.startActivity(intent)
            }
        }
    }
}