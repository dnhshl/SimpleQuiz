package com.example.simplequiz

import android.util.Log

class QuizStateMachine {

    var currentState: QuizState = QuizState.LOGGED_OUT

    enum class QuizState {
        LOGGED_OUT, IDLE, STARTING, PLAYING, GAME_OVER
    }

    enum class QuizEvent {
        LOGIN, START_GAME, LOGOUT, GAME_PREPARED, TIMER_UP,
        QUESTIONS_UP, WRONG_ANSWER, CORRECT_ANSWER, GAME_FINISHED
    }

    fun stateTransaction(event: QuizEvent) {
        Log.i(">>> StateMachine", "Event: $event")
        currentState = when (currentState) {
            QuizState.LOGGED_OUT -> when (event) {
                QuizEvent.LOGIN -> QuizState.IDLE
                else -> currentState
            }
            QuizState.IDLE -> when (event) {
                QuizEvent.START_GAME -> QuizState.STARTING
                QuizEvent.LOGOUT -> QuizState.LOGGED_OUT
                else -> currentState
            }
            QuizState.STARTING -> when (event) {
                QuizEvent.GAME_PREPARED -> QuizState.PLAYING
                QuizEvent.LOGOUT -> QuizState.LOGGED_OUT
                else -> currentState
            }
            QuizState.PLAYING -> when (event) {
                QuizEvent.TIMER_UP,
                QuizEvent.QUESTIONS_UP,
                QuizEvent.WRONG_ANSWER -> QuizState.GAME_OVER
                QuizEvent.CORRECT_ANSWER -> QuizState.PLAYING
                QuizEvent.LOGOUT -> QuizState.LOGGED_OUT
                else -> currentState
            }
            QuizState.GAME_OVER -> when (event) {
                QuizEvent.GAME_FINISHED -> QuizState.IDLE
                QuizEvent.LOGOUT -> QuizState.LOGGED_OUT
                else -> currentState
            }
        }
    }
}
