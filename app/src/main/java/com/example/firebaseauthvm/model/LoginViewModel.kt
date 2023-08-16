package com.example.firebaseauthvm.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private val _loginState by lazy { MutableLiveData<LoginState>(LoginState.LoggedOut) }
    val loginState: LiveData<LoginState>
        get() = _loginState

    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    init {
        auth = Firebase.auth
        // Create the AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                _loginState.value = LoginState.LoggedIn
                Log.i(">>>>", "onAuthStateChanged:signed_in:" + user.uid + user.email)
            } else {
                _loginState.value = LoginState.LoggedOut
                Log.i(">>>>", "onAuthStateChanged:signed_out")
            }
        }
        auth.addAuthStateListener(authStateListener)
    }


    fun signUp(email: String, password: String) {
        _loginState.value = LoginState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(">>>","Email signup is successful")
                    _loginState.value = LoginState.LoggedIn
                } else {
                    task.exception?.let {
                        Log.i(">>>>","Email signup failed with error: ${it.localizedMessage}")
                        _loginState.value = LoginState.LoginError(it.localizedMessage)
                    }
                }
            }
    }

    fun signIn(email: String, password: String) {
        _loginState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(">>>","Login is successful")
                    //_loginState.value = LoginState.LoggedIn
                } else {
                    task.exception?.let {
                        Log.i(">>>>","Login failed with error: ${it.localizedMessage}")
                        _loginState.value = LoginState.LoginError(it.localizedMessage)
                    }
                }
            }
    }

    fun logout() {
        auth.signOut()
    }


    fun sendPwResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _loginState.value = LoginState.PwReset
                    Log.i(">>>>", "Reset Email send")
                } else {
                    _loginState.value = LoginState.LoginError(it.exception!!.message.toString())
                    Log.i(">>>>", it.exception!!.message.toString())
                }
            }
    }

    fun getUser(): String {
        return auth.currentUser?.email ?: ""
    }
}