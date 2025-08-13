package com.midas.whatsapp.View

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.midas.whatsapp.R
import com.midas.whatsapp.ViewModel.LoginViewModel
import com.midas.whatsapp.ViewModel.UserProfileViewModel
import com.midas.whatsapp.databinding.ActivityChangeNameBinding

class ChangeNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeNameBinding
    private val userViewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangeNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpProfileDataAndUi()
        setUpListeners()
    }

    private fun setUpProfileDataAndUi(){
        userViewModel.userProfile.observe(this){user ->
            if(user != null){
                binding.newUserNameEditText.setText(user.displayName)
            }
        }
    }

    private fun setUpListeners(){
        binding.saveButton.setOnClickListener {
            val newName = binding.newUserNameEditText.text.toString()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            userViewModel.updateUserName(newName)
            finish()
        }
    }
}