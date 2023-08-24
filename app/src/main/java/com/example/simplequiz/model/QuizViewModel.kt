package com.example.simplequiz.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplequiz.QuizStateMachine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Timer
import java.util.TimerTask

class QuizViewModel : ViewModel() {

    // Zustandsautomat

    private val stateMachine = QuizStateMachine()

    private val _currentState = MutableLiveData<QuizStateMachine.QuizState>()
    val currentState: LiveData<QuizStateMachine.QuizState>
        get() = _currentState

    fun doStateTransaction(event: QuizStateMachine.QuizEvent) {
            stateMachine.stateTransaction(event)
            _currentState.value = stateMachine.currentState
    }


}