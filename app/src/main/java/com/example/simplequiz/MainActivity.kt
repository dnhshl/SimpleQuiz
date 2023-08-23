package com.example.simplequiz

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.viewModels
import com.example.simplequiz.databinding.ActivityMainBinding
import com.example.simplequiz.model.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val authvm: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (item.itemId) {
            R.id.action_nickname -> {
                if (authvm.getUser() == null) {
                    showErrorSnackbar(getString(R.string.notLoggedIn))
                } else {
                    setNicknameDialog()
                }
                true
            }
            R.id.action_login -> {
                navController.navigate(R.id.signInFragment)
                true
            }
            R.id.action_highscore -> {
                navController.navigate(R.id.highscoreFragment)
                true
            }
            R.id.action_quiz -> {
                navController.navigate(R.id.quizFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("OK") {}
            .show()
    }

    private fun setNicknameDialog() {
        val editTextNickname = EditText(this)
        editTextNickname.hint = getString(R.string.nicknameHint)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.setNickname_title))
            .setMessage(getString(R.string.setNickname_msg))
            .setView(editTextNickname)
            .setPositiveButton("OK") { dialog, _ ->
                val nickname = editTextNickname.text.toString().trim()
                if (nickname.isNullOrEmpty()) {
                    showErrorSnackbar(getString(R.string.noValidInput))
                } else {
                    authvm.updateNickname(nickname)
                    Log.i(">>> Input Dialog", "Input: $nickname")
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        alertDialog.show()
    }
}