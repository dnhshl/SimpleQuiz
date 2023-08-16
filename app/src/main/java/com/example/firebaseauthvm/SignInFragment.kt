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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseauthvm.databinding.FragmentSigninBinding
import com.example.firebaseauthvm.model.LoginViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SignInFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (validateForm(email, password)) {
                vm.signUp(email, password)
                findNavController().navigate(R.id.loginStatusFragment)
            }
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (validateForm(email, password)) {
                vm.signIn(email, password)
                findNavController().navigate(R.id.loginStatusFragment)
            }
        }
        binding.textViewPWReset.setOnClickListener {
            resetPwDialog()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun resetPwDialog() {
        val editTextEmail = EditText(context)
        editTextEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        editTextEmail.hint = getString(R.string.emailHint)

        val alertDialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.forgotPw_title))
            .setMessage(getString(R.string.forgotPw_msg))
            .setView(editTextEmail)
            .setPositiveButton("OK") { dialog, _ ->
                val email = editTextEmail.text.toString()
                vm.sendPwResetEmail(email)
                // Handle the text input here
                Log.i(">>> Input Dialog", "Input: $email")
                dialog.dismiss()
                findNavController().navigate(R.id.loginStatusFragment)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        alertDialog.show()
    }


    private fun validateForm(email: String, password: String): Boolean {

        var valid = true
        if (email.isEmpty()) {
            binding.editTextEmail.error = getString(R.string.required)
            valid = false
        }

        if (password.isEmpty()) {
            binding.editTextPassword.error = getString(R.string.required)
            valid = false
        }

        return valid
    }
}