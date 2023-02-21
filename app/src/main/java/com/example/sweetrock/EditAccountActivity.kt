package com.example.sweetrock

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.sweetrock.databinding.ActivityEditAccountBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class EditAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pfpRef: StorageReference

    private lateinit var photoLauncher: ActivityResultLauncher<Intent>
    private lateinit var photoUri: Uri
    private var photoChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.edit_account_title)
        auth = Firebase.auth
        pfpRef = Firebase.storage.reference.child("images")

        photoUri = Uri.parse("android.resource://my.package.name/" + R.drawable.default_pfp) // this is actually invalid

        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                photoChanged = true
                photoUri = res.data?.data!!
                binding.account.editPfp.setImageURI(photoUri)
                updateButton()
            }
        }

        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("Account", "Something very bad happened somehow")
            finish()


        } else {
            binding.account.editName.setText(currentUser.displayName)

            Picasso.get().load(currentUser.photoUrl).placeholder(R.drawable.default_pfp).into(binding.account.editPfp)

            binding.account.editName.doOnTextChanged { _, _, _, _ ->
                updateButton()
            }


            binding.account.editPfp.setOnClickListener { v ->
                val imageIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }

                photoLauncher.launch(imageIntent)
            }

            binding.account.editUpdateButton.isEnabled = false
            binding.account.editUpdateButton.setOnClickListener { v ->
                binding.account.editUpdateButton.isEnabled = false

                if (photoChanged) {
                    val uploadTask = (pfpRef.child(auth.currentUser!!.uid)).putFile(photoUri)

                    uploadTask.addOnFailureListener {
                        Log.e("spong", "spong the photo failed")
                        Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_edit_failed_snackbar_text, Snackbar.LENGTH_SHORT).show()
                        finish()
                    }.addOnSuccessListener {
                        pfpRef.child(auth.currentUser!!.uid).downloadUrl.addOnSuccessListener { uri ->
                            val profileUpdates = userProfileChangeRequest {
                                displayName = binding.account.editName.text.toString()
                                photoUri = uri
                            }

                            auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("spong", "User profile updated.")
                                    Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_edit_snackbar_text, Snackbar.LENGTH_SHORT).show()
                                    finish()
                                }
                            }

                            finish()
                        }.addOnFailureListener {
                            Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_edit_failed_snackbar_text, Snackbar.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = binding.account.editName.text.toString()
                    }

                    auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("spong", "User profile (no pciture) updated.")
                            Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_edit_snackbar_text, Snackbar.LENGTH_SHORT).show()
                            finish()
                        }
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


    private fun updateButton() {
        binding.account.editUpdateButton.isEnabled = binding.account.editName.text!!.isNotBlank() ||  photoChanged
    }
}