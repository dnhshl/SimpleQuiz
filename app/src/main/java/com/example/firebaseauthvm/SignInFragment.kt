package com.example.firebaseauthvm

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseauthvm.databinding.FragmentSigninBinding
import com.example.firebaseauthvm.model.LoginState
import com.example.firebaseauthvm.model.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.integrity.internal.l

class SignInFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val authvm: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observe loginState for errors
        authvm.loginState.observe(viewLifecycleOwner) { loginState ->
            Log.i(">>>Fragment:", "loginState changed $loginState")
            when (loginState) {
                is LoginState.LoginError ->
                    showErrorSnackbar(loginState.message ?: getString(R.string.error))
                LoginState.PwReset ->
                    showErrorSnackbar(getString(R.string.pwresetting))
                else -> {}
            }
        }

        // observe user to display login status
        authvm.user.observe(viewLifecycleOwner) { user ->
            if (user == null){
                binding.tvStatus.text = getString(R.string.loggedout)
            } else {
                var statusmsg = getString(R.string.loggedin).format(user.email)
                if (!user.displayName.isNullOrEmpty()) statusmsg += " (${user.displayName})"
                binding.tvStatus.text = statusmsg
            }
        }

        binding.btnLogin.setOnClickListener {
            signInOrUpDialog(signIn = true)
        }

        binding.btnSignUp.setOnClickListener {
            signInOrUpDialog(signIn = false)
        }

        binding.btnLogout.setOnClickListener {
            authvm.logout()
        }

        binding.btnResetPassword.setOnClickListener {
            resetPwDialog()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // alert Dialog with the option to enter Email address
    private fun resetPwDialog() {
        val editTextEmail = EditText(context)
        editTextEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        editTextEmail.hint = getString(R.string.emailHint)

        val alertDialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.forgotPw_title))
            .setMessage(getString(R.string.forgotPw_msg))
            .setView(editTextEmail)
            .setPositiveButton("OK") { dialog, _ ->
                val email = editTextEmail.text.toString().trim()
                if (email.isNullOrEmpty()) {
                    showErrorSnackbar(getString(R.string.noValidInput))
                } else {
                    authvm.sendPwResetEmail(email)
                    Log.i(">>> Input Dialog", "Input: $email")
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        alertDialog.show()
    }

    // alert Dialog with the option to enter E-mail and PW
    private fun signInOrUpDialog(signIn: Boolean = true) {

        var title = getString(R.string.signInTitle)
        var msg = getString(R.string.signInMessage)
        if (!signIn) {
            title = getString(R.string.signUpTitle)
            msg = getString(R.string.signUpMessage)
        }

        val alertDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null)
        val editTextEmail = alertDialogView.findViewById<EditText>(R.id.editTextEmail)
        //editTextEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        editTextEmail.hint = getString(R.string.emailHint)
        val editTextPassword = alertDialogView.findViewById<EditText>(R.id.editTextPassword)
        //editTextPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        editTextPassword.hint = getString(R.string.pwHint)


        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setView(alertDialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val email = editTextEmail.text.toString().trim()
                val password = editTextPassword.text.toString().trim()
                Log.i(">>> Input Dialog", "Input: $email $password")
                if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                    showErrorSnackbar(getString(R.string.noValidInput))
                } else {
                    // Perform sign-in or sign-up actions
                    if (signIn) authvm.signIn(email, password)
                    else authvm.signUp(email, password)
                    // Handle the text input here

                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        alertDialog.show()
    }

    fun showErrorSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAction("OK") {}
            .show()
    }


}