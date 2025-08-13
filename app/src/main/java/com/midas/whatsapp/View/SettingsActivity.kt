package com.midas.whatsapp.View

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.midas.whatsapp.Model.data.SettingsItem
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.SettingsAdapter
import com.midas.whatsapp.ViewModel.LoginViewModel
import com.midas.whatsapp.ViewModel.UserProfileViewModel
import com.midas.whatsapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsData: List<SettingsItem>
    private val viewModel: UserProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpData()
        setUpRecyclerView()
        setUpListeners()
    }

    override fun onStart() {
        super.onStart()
        viewModel.userProfile.observe(this){ user ->
            if(user != null){
                binding.tvUserName.text = user.displayName
            }

        }
    }

    private fun setUpData(){
        settingsData = listOf(
            SettingsItem(R.drawable.ic_key, "Account", "Security notifications, change number", "ACCOUNT"),
            SettingsItem(R.drawable.ic_lock, "Privacy", "Block contacts, disappearing messages", "PRIVACY"),
            SettingsItem(R.drawable.ic_avatar, "Avatar", "Create, edit, profile photo", "AVATAR"),
            SettingsItem(R.drawable.ic_list, "Lists", "Manage people and groups", "LISTS"),
            SettingsItem(R.drawable.ic_chat, "Chats", "Theme, wallpapers, chat history", "CHATS"),
            SettingsItem(R.drawable.ic_bell, "Notifications", "Message, group & call tones", "NOTIFICATIONS"),
            SettingsItem(R.drawable.ic_storage, "Storage and data", "Network usage, auto-download", "STORAGE"),
            SettingsItem(R.drawable.ic_accessibility, "Accessibility", "Increase contrast, animation", "ACCESSIBILITY"),
            SettingsItem(R.drawable.ic_language, "App language", "English (device's language", "LANGUAGE"),
            SettingsItem(R.drawable.ic_help, "Help", "Help centre, contact us, privacy policy", "HELP"),
            SettingsItem(R.drawable.ic_invite, "Invite a friend", null, "INVITE"),
        )

    }

    private fun setUpRecyclerView(){
        val recyclerView = binding.settingsRecyclerView

        val settingsAdapter = SettingsAdapter(settingsData){user ->}
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = settingsAdapter
    }

    private fun setUpListeners(){
        binding.profileConstraint.setOnClickListener {
            val intent = Intent(this@SettingsActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}