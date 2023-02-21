package com.example.sweetrock

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.Date

class ReviewsRecyclerAdapter(private val reviews: ArrayList<Review>) : RecyclerView.Adapter<ReviewsRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_reviews_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var imageView: ImageView
        private var nameTextView: TextView
        private var ratingBar: RatingBar
        private var dateTextView: TextView
        private var textView: TextView

        init {
            imageView = view.findViewById(R.id.reviews_item_image_view)
            nameTextView = view.findViewById(R.id.reviews_item_name_text_view)
            ratingBar = view.findViewById(R.id.reviews_item_rating_bar)
            dateTextView = view.findViewById(R.id.reviews_item_date_text_view)
            textView = view.findViewById(R.id.reviews_item_text_view)
        }

        fun bind(review: Review) {

            nameTextView.text = review.username
            ratingBar.rating = review.rating
            dateTextView.text = DateUtils.getRelativeTimeSpanString(review.date.time, Date().time, 0)
            textView.text = review.text

            Firebase.storage.reference.child("images/${review.uid}").downloadUrl.addOnSuccessListener {  uri ->
                Picasso.get().load(uri).placeholder(R.drawable.default_pfp).into(imageView)
            }.addOnFailureListener {
                Log.e("spong", "spong (no pfp issue) $it") //TODO do something about this?
                Picasso.get().load(R.drawable.default_pfp).into(imageView)
            }
        }
    }
}