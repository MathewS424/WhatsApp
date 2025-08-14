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
import com.midas.whatsapp.databinding.ActivityLoginBinding
import com.midas.whatsapp.util.CustomResult

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setUpListeners()
        setUpObservers()
    }

    fun setUpListeners(){
        loginBinding.logInBtn.setOnClickListener {

            val email = loginBinding.editTextLogInEmail.text.toString().trim()
            val password = loginBinding.editTextLogInPassword.text.toString().trim()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this@LoginActivity, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.loginUser(email, password)
        }

        loginBinding.textViewSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun setUpObservers(){
        viewModel.loginResult.observe(this@LoginActivity){ result ->
            when(result){
                is CustomResult.Success -> {
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is CustomResult.Failure -> {
                    Toast.makeText(this@LoginActivity, "Login failed: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.isLoading.observe(this@LoginActivity){ isLoading ->

            with(loginBinding){
                whatsappProgressBarLogIn.visibility = if(isLoading) View.VISIBLE else View.INVISIBLE
                logInBtn.isEnabled = !isLoading
                textViewSignUp.isEnabled = !isLoading
                editTextLogInEmail.isEnabled = !isLoading
                editTextLogInPassword.isEnabled = !isLoading
            }
        }
    }


}