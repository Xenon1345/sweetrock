package com.example.sweetrock

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.sweetrock.databinding.FragmentAccountBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

//    private lateinit var loginLauncher : ActivityResultLauncher<Intent>
    private lateinit var updateLauncher : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        auth = Firebase.auth

//        loginLauncher = registerForActivityResult(
//            FirebaseAuthUIActivityResultContract()
//        ) { res ->
//            val response = res.idpResponse
//            if (res.resultCode == Activity.RESULT_OK) {
//                update()
//                Snackbar.make(binding.root, R.string.account_signed_in_snackbar_text, Snackbar.LENGTH_SHORT)
//                    .setAnchorView(R.id.bottom_navigation)
//                    .show()
//            } else {
//                if (response != null)
//                    Snackbar.make(binding.root, R.string.account_sign_in_failed_snackbar_text, Snackbar.LENGTH_SHORT)
//                        .setAnchorView(R.id.bottom_navigation)
//                        .show()
//            }
//        }

        updateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            update()
        }

        update()

        binding.accountTabLogoutButton.setOnClickListener { v ->
            val builder = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_SweetRock_MaterialAlertDialog)
            builder
                .setTitle(getString(R.string.account_log_out_dialog_title))
                .setMessage(getString(R.string.account_log_out_dialog_message))
                .setPositiveButton(getString(R.string.account_log_out_positive_button)) { dialog, _ ->
                    v.isEnabled = false

                    binding.accountTab.animate()
                        .alpha(0f)
                        .setDuration(1000L)
                        .setListener(null)


                    AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
                        update()
                        Snackbar.make(binding.root, R.string.account_logged_out_snackbar_text, Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.bottom_navigation)
                            .show()
                        binding.accountTab.animate()
                            .alpha(1f)
                            .setDuration(1000L)
                            .setListener(null)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.account_log_out_negative_button)) { dialog, _ ->
                    dialog.cancel()
                }
            builder.show()
        }

        binding.accountTabLoginButton.setOnClickListener { _ ->
//            loginLauncher.launch(
//                AuthUI.getInstance()
//                    .createSignInIntentBuilder()
//                    .setAvailableProviders(
//                    arrayListOf(AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build())
//                    ).setIsSmartLockEnabled(false)
//                    .setTheme(R.style.Theme_SweetRock)
//                    .build()
//            )
            updateLauncher.launch(Intent(context, LoginActivity::class.java))
        }

        binding.accountTabInfo.setOnClickListener { _ ->
            updateLauncher.launch(Intent(context, EditAccountActivity::class.java))
        }

        binding.accountTabSignUpButton.setOnClickListener { _ ->
            updateLauncher.launch(Intent(context, SignUpActivity::class.java))
        }

        return binding.root
    }

    override fun onResume() {
        update()
        super.onResume()
    }

    private fun update() {
        val currentUser = auth.currentUser
        binding.accountTabInfo.isClickable = currentUser != null
        if (currentUser != null) {
            binding.accountTabName.text = currentUser.displayName
            binding.accountTabEmail.text = currentUser.email
            Picasso.get().load(currentUser.photoUrl).placeholder(R.drawable.default_pfp).into(binding.accountTabImageView)

            binding.accountTabLogoutButton.apply {
                isVisible = true
                isEnabled = true
            }

            binding.accountTabLoginButton.apply {
                isVisible = false
                isEnabled = false
            }
            binding.accountTabSignUpButton.apply {
                isVisible = false
                isEnabled = false
            }

        } else {
            binding.accountTabName.text = getString(R.string.account_not_signed_in_title)
            binding.accountTabEmail.text = getString(R.string.account_not_signed_in_caption)
            Picasso.get().load(R.drawable.default_pfp).into(binding.accountTabImageView)

            binding.accountTabLogoutButton.apply {
                isVisible = false
                isEnabled = false
            }
            binding.accountTabLoginButton.apply {
                isVisible = true
                isEnabled = true
            }
            binding.accountTabSignUpButton.apply {
                isVisible = true
                isEnabled = true
            }
        }
    }


}