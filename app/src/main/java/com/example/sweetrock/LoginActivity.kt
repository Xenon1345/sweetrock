package com.example.sweetrock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import androidx.core.widget.doOnTextChanged
import com.example.sweetrock.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.login_title)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth

        binding.login.email.doOnTextChanged { _, _, _, _ -> updateButton() }
        binding.login.password.doOnTextChanged { _, _, _, _ -> updateButton() }


        binding.login.loginButton.setOnClickListener { v ->
            if(validateForm()) {
                binding.login.loginButton.isEnabled = false
                auth.signInWithEmailAndPassword(binding.login.email.text.toString(), binding.login.password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("spong", "signInWithEmail:success")
                            Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_signed_in_snackbar_text, Snackbar.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.e("spong", "signInWithEmail:failure", task.exception)
                            if (task.exception is FirebaseAuthInvalidUserException) {
                                binding.login.emailLayout.error = getString(R.string.error_email_wrong)
                            } else if(task.exception is FirebaseAuthInvalidCredentialsException) {
                                binding.login.passwordLayout.error = getString(R.string.error_password_wrong)
                            }
                            Snackbar.make(MainActivity.instance.findViewById(R.id.bottom_navigation), R.string.account_sign_in_failed_snackbar_text, Snackbar.LENGTH_SHORT).show()
                            updateButton()
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

    private fun validateForm(): Boolean {
        with(binding.login) {
            emailLayout.error = null
            passwordLayout.error = null

            if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) emailLayout.error = getString(R.string.error_invalid_email)
            if (email.text!!.isBlank()) emailLayout.error = getString(R.string.error_empty)
            if (password.text!!.isBlank()) passwordLayout.error = getString(R.string.error_empty)


            return Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() &&
                    email.text!!.isNotBlank() &&
                    password.text!!.isNotBlank()

        }
    }

    private fun updateButton() {
        binding.login.loginButton.isEnabled = (binding.login.email.text!!.isNotBlank() && binding.login.password.text!!.isNotBlank())
    }
}