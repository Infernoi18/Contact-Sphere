package com.example.contactsphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactsphere.databinding.ActivityLoginBinding
import com.example.contactsphere.utils.PrefsManager

class LoginActivity : AppCompatActivity() {

    private val tag = "LoginActivityLifecycle"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate called")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)

        if (prefsManager.isLoggedIn()) {
            navigateToWelcome(prefsManager.getUsername())
            return
        }

        if (prefsManager.isRememberMeEnabled()) {
            binding.etUsername.setText(prefsManager.getUsername())
            binding.etPassword.setText(prefsManager.getSavedPassword())
            binding.cbRememberMe.isChecked = true
        }

        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                val username = binding.etUsername.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                val rememberMe = binding.cbRememberMe.isChecked

                prefsManager.saveUser(username, password, rememberMe)
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                navigateToWelcome(username)
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        } else if (username.length < 4) {
            binding.tilUsername.error = "Username must be at least 4 characters"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun navigateToWelcome(username: String) {
        val intent = Intent(this, WelcomeActivity::class.java).apply {
            putExtra("EXTRA_USERNAME", username)
        }
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(tag, "onRestart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy called")
    }
}
