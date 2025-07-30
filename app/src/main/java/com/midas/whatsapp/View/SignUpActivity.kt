package com.midas.whatsapp.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.midas.whatsapp.R
import com.midas.whatsapp.ViewModel.LoginViewModel
import com.midas.whatsapp.ViewModel.SignUpViewModel
import com.midas.whatsapp.databinding.ActivitySignUpBinding
import com.midas.whatsapp.util.CustomResult

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpBinding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val signInViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupObservers()

    }

    override fun onStart() {
        super.onStart()
        signInViewModel.currentUser.observe(this) { user ->
            if (user != null){
                Toast.makeText(this@SignUpActivity, "Welcome back, ${user.email}!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun setupListeners() {
        signUpBinding.registerBtn.setOnClickListener {
            val email = signUpBinding.editTextSignUpEmail.text.toString().trim()
            val password = signUpBinding.editTextSignUpPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Password must be at least 6 characters long",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            signUpViewModel.registerUser(email, password)
        }

        signUpBinding.textViewLogIn.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupObservers() {
        signUpViewModel.registrationResult.observe(this) { result ->
            when (result) {
                is CustomResult.Success -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is CustomResult.Failure -> {
                    Toast.makeText(
                        this,
                        "Registration failed: ${result.exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        signUpViewModel.isLoading.observe(this) { isLoading ->
            signUpBinding.whatsappProgressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
            signUpBinding.registerBtn.isEnabled = !isLoading
            signUpBinding.editTextSignUpEmail.isEnabled = !isLoading
            signUpBinding.editTextSignUpPassword.isEnabled = !isLoading
        }
    }



}