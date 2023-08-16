package com.example.firebaseauthvm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.firebaseauthvm.databinding.FragmentLoginstatusBinding
import com.example.firebaseauthvm.model.LoginState
import com.example.firebaseauthvm.model.LoginViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginStatusFragment : Fragment() {

    private var _binding: FragmentLoginstatusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginstatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.loginState.observe(viewLifecycleOwner) { loginState ->
                when (loginState) {
                    LoginState.LoggedOut ->
                        binding.tvLogStatus.text = getString(R.string.loggedout)
                    LoginState.LoggedIn  ->
                        binding.tvLogStatus.text = getString(R.string.loggedin).format(vm.getUser())
                    LoginState.Loading   ->
                        binding.tvLogStatus.text = getString(R.string.processing)
                    LoginState.PwReset   ->
                        binding.tvLogStatus.text = getString(R.string.pwresetting)
                    is LoginState.LoginError ->
                        binding.tvLogStatus.text = getString(R.string.loggederror).format(loginState.message)
                }
         }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}