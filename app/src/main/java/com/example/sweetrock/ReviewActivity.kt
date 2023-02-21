package com.example.sweetrock

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.sweetrock.databinding.ActivityReviewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*


class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        auth = Firebase.auth

        title = "Leave a review"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.review.reviewUsernameTextView.text = auth.currentUser!!.displayName
        binding.review.reviewEmailTextView.text = auth.currentUser!!.email
        Picasso.get().load(auth.currentUser!!.photoUrl).placeholder(R.drawable.default_pfp).into(binding.review.reviewPfpImageView)


        binding.review.reviewRatingBar.setOnRatingBarChangeListener { _, _, _ ->
            buttonUpdate()
        }

        binding.review.reviewEditText.doOnTextChanged { _, _, _, _ ->
            buttonUpdate()
        }

        binding.review.reviewPostButton.isEnabled = false
        binding.review.reviewPostButton.setOnClickListener {
            db.collection("reviews")
                .add(
                    hashMapOf(
                        "username" to auth.currentUser!!.displayName!!,
                        "uid" to auth.currentUser!!.uid,
                        "date" to Date(),
                        "rating" to binding.review.reviewRatingBar.rating.toDouble(),
                        "text" to binding.review.reviewEditText.text.toString()
                    )
                )
                .addOnSuccessListener {
                    finish()
                    Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.review_post_success_text, Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.bottom_navigation)
                        .show()
                }
                .addOnFailureListener {
                    Snackbar.make(binding.root, R.string.review_post_failure_text, Snackbar.LENGTH_SHORT)
                        .show()
                }
        }
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


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(imm.isAcceptingText) imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        return true
    }

    private fun buttonUpdate() {
        binding.review.reviewPostButton.isEnabled =
            binding.review.reviewRatingBar.rating > 0 &&
                    binding.review.reviewEditText.text!!.isNotBlank()
    }
}