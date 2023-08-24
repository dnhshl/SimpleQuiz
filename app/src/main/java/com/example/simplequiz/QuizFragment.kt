package com.example.simplequiz

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.simplequiz.databinding.FragmentQuizBinding
import com.example.simplequiz.firestore.Highscoredata
import com.example.simplequiz.model.FirestoreViewModel
import com.example.simplequiz.model.LoginViewModel
import com.example.simplequiz.model.QuizViewModel


class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val authvm: LoginViewModel by activityViewModels()
    private val quizvm: QuizViewModel by activityViewModels()
    private val dbvm: FirestoreViewModel by activityViewModels()

    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false

    private var correctAnswer = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observe user to display login status
        authvm.user.observe(viewLifecycleOwner) { user ->
            Log.i(">>>", "Current User: $user")
            if (user == null) {
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.LOGOUT)
            } else {
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.LOGIN)
            }
        }


        dbvm.quizdata.observe(viewLifecycleOwner) { quizdata ->
            correctAnswer = quizdata.answer
            if (quizdata.answer.equals("questions_up")) {
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.QUESTIONS_UP)
            } else {
                binding.textViewMain.text = quizdata.question
            }
        }

        quizvm.points.observe(viewLifecycleOwner) { points ->
            binding.textViewNumbers.text = points.toString()
        }

        binding.buttonPlay.setOnClickListener {
            quizvm.doStateTransaction(QuizStateMachine.QuizEvent.START_GAME)
        }

        binding.buttonTrue.setOnClickListener {
            if (correctAnswer.equals("wahr")) {
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.CORRECT_ANSWER)
            } else {
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.WRONG_ANSWER)
            }
        }
        binding.buttonFalse.setOnClickListener {
            if (correctAnswer.equals("falsch")) {
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.CORRECT_ANSWER)
            } else {
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.WRONG_ANSWER)
            }
        }
    }

    private fun setUiLoggedOut() {
        binding.textViewMain.isVisible = true
        binding.buttonPlay.isVisible = false
        binding.textViewTimer.isVisible = false
        binding.textViewNumbers.isVisible = false
        binding.tvPunkte.isVisible = false
        binding.buttonFalse.isVisible = false
        binding.buttonTrue.isVisible = false
    }

    private fun setUiIdle() {
        binding.textViewMain.isVisible = true
        binding.buttonPlay.isVisible = true
        binding.textViewTimer.isVisible = false
        binding.textViewNumbers.isVisible = false
        binding.tvPunkte.isVisible = false
        binding.buttonFalse.isVisible = false
        binding.buttonTrue.isVisible = false
    }

    private fun setUiStarting() {
        binding.textViewMain.isVisible = true
        binding.buttonPlay.isVisible = false
        binding.textViewTimer.isVisible = true
        binding.textViewNumbers.isVisible = true
        binding.tvPunkte.isVisible = true
        binding.buttonFalse.isVisible = true
        binding.buttonTrue.isVisible = true
    }

    private fun setUiPlaying() {
        binding.textViewMain.isVisible = true
        binding.buttonPlay.isVisible = false
        binding.textViewTimer.isVisible = true
        binding.textViewNumbers.isVisible = true
        binding.tvPunkte.isVisible = true
        binding.buttonFalse.isVisible = true
        binding.buttonTrue.isVisible = true
    }
    private fun setUiGameOver() {
        binding.textViewMain.isVisible = false
        binding.buttonPlay.isVisible = false
        binding.textViewTimer.isVisible = true
        binding.textViewNumbers.isVisible = true
        binding.tvPunkte.isVisible = true
        binding.buttonFalse.isVisible = true
        binding.buttonTrue.isVisible = true
    }

    private fun sideEffectLoggedOut() {
        binding.textViewMain.text = getString(R.string.loggedout)
    }

    private fun sideEffectIdle() {
        val user = authvm.getUser()
        binding.textViewMain.text = getString(R.string.loggedin).format(user!!.displayName)
        dbvm.setPersonalHighscoreCollectionRef(user!!.uid)
    }

    private fun sideEffectStarting() {
        startTimer()
        dbvm.shuffleAndResetQuizdata()
        quizvm.resetPoints()
    }

    private fun sideEffectPlaying() {
        dbvm.nextQuestion()
        quizvm.countPoint()
    }

    private fun sideEffectGameOver() {
        stopTimer()
        gameOverDialog()
    }

    // Helper Funktions

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textViewTimer.text = (millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                binding.textViewTimer.text = "0"
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.TIMER_UP)
            }
        }

        quizvm.doStateTransaction(QuizStateMachine.QuizEvent.GAME_PREPARED)
        countDownTimer.start()
        isTimerRunning = true
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel()
            isTimerRunning = false
        }
    }


    private fun gameOverDialog() {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.gameOverTitle))
            .setMessage(getString(R.string.gameOverMessage).format(quizvm.getPoints().toString()))
            .setPositiveButton(getString(R.string.saveHighscore)) { dialog, _ ->
                dbvm.writeDataToFirestore(
                    Highscoredata(
                        userName = authvm.getUser()?.displayName ?: "noname",
                        userScore = quizvm.getPoints()
                    )
                )
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.GAME_FINISHED)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dontSaveHighscore)) { dialog, _ ->
                quizvm.doStateTransaction(QuizStateMachine.QuizEvent.GAME_FINISHED)
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()

        quizvm.currentState.observe(viewLifecycleOwner) { currentState ->
            Log.i(">>>", "setUI and do sideEffects for state $currentState")
            when (currentState) {
                QuizStateMachine.QuizState.LOGGED_OUT -> {
                    setUiLoggedOut()
                    sideEffectLoggedOut()
                }
                QuizStateMachine.QuizState.IDLE -> {
                    setUiIdle()
                    sideEffectIdle()
                }
                QuizStateMachine.QuizState.STARTING -> {
                    setUiStarting()
                    sideEffectStarting()
                }
                QuizStateMachine.QuizState.PLAYING -> {
                    setUiPlaying()
                    sideEffectPlaying()
                }
                QuizStateMachine.QuizState.GAME_OVER -> {
                    setUiGameOver()
                    sideEffectGameOver()
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        _binding = null
    }
}
