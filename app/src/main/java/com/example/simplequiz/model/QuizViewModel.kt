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
        Log.i(">>>", "Event $event changes ${stateMachine.currentState} ...")
        stateMachine.stateTransaction(event)
        _currentState.value = stateMachine.currentState
        Log.i(">>>", "... to ${stateMachine.currentState}")

    }

    // Punkte
    private val _points = MutableLiveData<Int>(-1)
    val points: LiveData<Int>
        get() = _points

    fun countPoint() {
        _points.value = _points.value!! + 1
    }

    fun resetPoints() {
        _points.value = -1
    }

    fun getPoints(): Int {
        return _points.value!!
    }
}