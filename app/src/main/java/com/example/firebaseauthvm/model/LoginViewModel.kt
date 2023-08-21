package com.example.firebaseauthvm.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?>
        get() = _user

    private val _loginState by lazy { MutableLiveData<LoginState>(LoginState.LoggedOut) }
    val loginState: LiveData<LoginState>
        get() = _loginState

    private val auth = Firebase.auth
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _user.value = auth.currentUser
        if (_user.value != null) {
            _loginState.value = LoginState.LoggedIn
            Log.i(">>>>vm", "onAuthStateChanged:signed_in")
        } else {
            _loginState.value = LoginState.LoggedOut
            Log.i(">>>>vm", "onAuthStateChanged:signed_out")
        }
    }

    init {
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
                        Log.i(">>>>vm","Email signup failed with error: ${it.localizedMessage}")
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
                        Log.i(">>>>vm","Login failed with error: ${it.localizedMessage}")
                        _loginState.value = LoginState.LoginError(it.localizedMessage)
                    }
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }


    fun sendPwResetEmail(email: String) {
        auth.signOut()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _loginState.value = LoginState.PwReset
                    Log.i(">>>>vm", "Reset Email send")
                } else {
                    _loginState.value = LoginState.LoginError(it.exception!!.message.toString())
                    Log.i(">>>>vm", it.exception!!.message.toString())
                }
            }
    }

    fun updateNickname(nickname: String) {
        _user.value?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build()
            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _user.value = it
                    } else {
                        Log.i(">>>", "Error setting display name")
                    }
                }
        }
    }

    fun getUser(): FirebaseUser? {
        return _user.value
    }
}