package com.example.sweetrock

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sweetrock.databinding.FragmentReviewsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class ReviewsFragment : Fragment() {
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private val reviews = ArrayList<Review>()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)

        db = Firebase.firestore
        auth = Firebase.auth

        db.collection("reviews")
            .addSnapshotListener { value, error ->
                android.util.Log.i("kill", error.toString())
                if (value != null) {
                    reviews.clear()
                    for (document in value) {
                        reviews.add(
                            Review(
                                document.getString("username")!!,
                                document.getString("uid")!!,
                                document.getDate("date")!!,
                                document.getDouble("rating")!!.toFloat(),
                                document.getString("text")!!
                            )
                        )
                    }
                    reviews.sortWith { first, second ->
                        second.date.compareTo(first.date)
                    }
                    binding.reviewsRecyclerView.adapter!!.notifyItemRangeChanged(0, reviews.size)
                }
            }
        binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.reviewsRecyclerView.adapter = ReviewsRecyclerAdapter(reviews)

        binding.reviewsButton.setOnClickListener {
            val intent = Intent(context, ReviewActivity::class.java)
            startActivity(intent)
        }

        auth.addAuthStateListener {
            if (auth.currentUser == null) {
                binding.reviewsButton.isEnabled = false
                binding.reviewsNotSignedInTextView.isVisible = true
            } else {
                binding.reviewsButton.isEnabled = true
                binding.reviewsNotSignedInTextView.isVisible = false
            }
        }

        return binding.root
    }
}