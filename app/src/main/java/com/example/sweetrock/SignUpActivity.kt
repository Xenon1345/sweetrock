package com.example.sweetrock

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import com.example.sweetrock.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var photoLauncher: ActivityResultLauncher<Intent>
    private lateinit var photoUri: Uri
    private var photoChanged = false

    private lateinit var pfpRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                photoChanged = true
                photoUri = res.data?.data!!
                binding.signUp.addPfp.setImageURI(photoUri)
            }
        }

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.sign_up_title)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth
        pfpRef = Firebase.storage.reference.child("images")

        Picasso.get().load(R.drawable.default_pfp).placeholder(R.drawable.default_pfp).into(binding.signUp.addPfp)

        binding.signUp.name.doOnTextChanged { _, _, _, _ -> updateButton() }
        binding.signUp.email.doOnTextChanged { _, _, _, _ -> updateButton() }
        binding.signUp.password.doOnTextChanged { _, _, _, _ -> updateButton() }

        binding.signUp.addPfp.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            photoLauncher.launch(imageIntent)
        }

        binding.signUp.signUpButton.setOnClickListener { v ->
            if(validateForm()) {
                binding.signUp.signUpButton.isEnabled = false

                auth.createUserWithEmailAndPassword(binding.signUp.email.text.toString(), binding.signUp.password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (photoChanged) {
                                val uploadTask = (pfpRef.child(auth.currentUser!!.uid)).putFile(photoUri)

                                uploadTask.addOnFailureListener {
                                    Log.e("spong", "upload failed: $it")
                                    Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_sign_up_failed_snackbar_text, Snackbar.LENGTH_SHORT).show()
                                    finish()
                                }.addOnSuccessListener {
                                    pfpRef.child(auth.currentUser!!.uid).downloadUrl.addOnSuccessListener{ uri ->
                                        auth.currentUser!!.updateProfile(userProfileChangeRequest {
                                            displayName = binding.signUp.name.text.toString()
                                            photoUri = uri
                                        }).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Log.d("crying tears of joy", "lik spong")
                                                Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_signed_up_snackbar_text, Snackbar.LENGTH_SHORT).show()
                                                finish()
                                            } else {
                                                Log.e("spongest", it.exception.toString())
                                                Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_sign_up_failed_snackbar_text, Snackbar.LENGTH_SHORT).show()
                                                finish()
                                            }
                                        }
                                    }
                                }
                            } else {
                                auth.currentUser!!.updateProfile(userProfileChangeRequest {
                                    displayName = binding.signUp.name.text.toString()
                                }).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Log.d("crying tears of joy", "lik spong")
                                        Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_signed_up_snackbar_text, Snackbar.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        Log.e("spongest", it.exception.toString())
                                        Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_sign_up_failed_snackbar_text, Snackbar.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }
                            }
                        } else {
                            Log.e("spongest", task.exception.toString())
                            Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_sign_up_failed_snackbar_text, Snackbar.LENGTH_SHORT).show()
                            finish()
                        }
                    }
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

    fun validateForm(): Boolean {
        with(binding.signUp) {
            nameLayout.error = null
            emailLayout.error = null
            passwordLayout.error = null

            if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) emailLayout.error = getString(R.string.error_invalid_email)
            if (email.text!!.isBlank()) emailLayout.error = getString(R.string.error_empty)
            if (name.text!!.isBlank()) nameLayout.error = getString(R.string.error_empty)
            if (password.text!!.isBlank()) passwordLayout.error = getString(R.string.error_empty)
            if (!password.text!!.contains(Regex("[0-9]"))) passwordLayout.error = getString(R.string.error_password_number)
            if (!password.text!!.contains(Regex("[a-zA-Z]"))) passwordLayout.error = getString(R.string.error_password_letter)
            if (password.text!!.length < 6) passwordLayout.error = getString(R.string.error_password_length)

            return Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() &&
                    email.text!!.isNotBlank() &&
                    name.text!!.isNotBlank() &&
                    password.text!!.isNotBlank() &&
                    password.text!!.length >= 6 &&
                    password.text!!.contains(Regex("[a-zA-Z]")) &&
                    password.text!!.contains(Regex("[0-9]"))
        }
    }

    fun updateButton() {
        binding.signUp.signUpButton.isEnabled = (binding.signUp.email.text!!.isNotBlank() &&
            binding.signUp.name.text!!.isNotBlank() &&
            binding.signUp.password.text!!.isNotBlank())
    }
}