package com.example.simplequiz.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplequiz.firestore.Highscoredata
import com.example.simplequiz.firestore.Quizdata
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Collections

class FirestoreViewModel : ViewModel() {


    private val quizdataCollectionRef = Firebase.firestore.collection("Library")

    private val highscoreCollectionRef = Firebase.firestore.collection("Leaderboard")
    private lateinit var personalHighscoreCollectionRef: CollectionReference

    // Daten als Ergebnis einer Abfrage aus der DB
    private var _highscoreList = MutableLiveData<List<Highscoredata>>()
    val highscoreList: LiveData<List<Highscoredata>>
        get() = _highscoreList


    fun setPersonalHighscoreCollectionRef(uid: String) {
        personalHighscoreCollectionRef =
            Firebase.firestore.collection("users")
                .document(uid)
                .collection("Leaderboard")
    }

    fun writeDataToFirestore(highscoredata: Highscoredata) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                highscoreCollectionRef.add(highscoredata).await()
                personalHighscoreCollectionRef.add(highscoredata).await()
            }
            catch(e: Exception) { Log.i(">>>", "Error writing Data: $e") }
        }
    }

    fun getHighscoredata(personal:Boolean = true) {

        viewModelScope.launch(Dispatchers.IO) {

            val collectionRef = when (personal) {
                true -> personalHighscoreCollectionRef
                else -> highscoreCollectionRef
            }
            try {
                val querySnapshot = collectionRef
                    .orderBy("userScore", Query.Direction.DESCENDING)
                    .limit(20)
                    .get().await()
                val dataList = mutableListOf<Highscoredata>()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(Highscoredata::class.java)
                    data?.let { dataList.add(it) }
                }
                withContext(Dispatchers.Main) {
                    _highscoreList.value = dataList
                }
            } catch(e: Exception) {
            Log.i(">>>", "Error retrieving data $e")
            }
        }
    }


    private val _quizdata = MutableLiveData<Quizdata>(Quizdata("",""))
    val quizdata: LiveData<Quizdata>
        get() = _quizdata

    private val quizdataList = mutableListOf<Quizdata>()
    private var currentIndex = 0

    init {
        subscribeToRealtimeUpdates()
    }

    private fun subscribeToRealtimeUpdates() {
        quizdataCollectionRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Log.i(">>>", "Realtime Update Error: $it")
                return@addSnapshotListener
            }
            querySnapshot?.let {
                quizdataList.clear()
                for(document in querySnapshot.documents) {
                    val data = document.toObject(Quizdata::class.java)
                    quizdataList.add(data!!)
                }
            }
        }
    }


    fun shuffleAndResetQuizdata () {
        Collections.shuffle(quizdataList)
        currentIndex = 0
    }

    fun nextQuestion() {
        Log.i(">>>dbvm:", "loadNextDocument $currentIndex of ${quizdataList.size}")
        if (currentIndex < quizdataList.size) {
            _quizdata.value = quizdataList[currentIndex]
            currentIndex++
        } else {
            _quizdata.value = Quizdata("", "questions_up")
        }
    }
 }





