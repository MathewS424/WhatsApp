package com.midas.whatsapp.View

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.midas.whatsapp.Model.data.SettingsItem
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.SettingsAdapter
import com.midas.whatsapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsData: List<SettingsItem>
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

    private fun setUpData(){
        settingsData = listOf(
            SettingsItem(R.drawable.ic_key, "Account", "Security notifications, change number"),
            SettingsItem(R.drawable.ic_lock, "Privacy", "Block contacts, disappearing messages"),
            SettingsItem(R.drawable.ic_avatar, "Avatar", "Create, edit, profile photo"),
            SettingsItem(R.drawable.ic_list, "Lists", "Manage people and groups"),
            SettingsItem(R.drawable.ic_chat, "Chats", "Theme, wallpapers, chat history"),
            SettingsItem(R.drawable.ic_bell, "Notifications", "Message, group & call tones"),
            SettingsItem(R.drawable.ic_storage, "Storage and data", "Network usage, auto-download"),
            SettingsItem(R.drawable.ic_accessibility, "Accessibility", "Increase contrast, animation"),
            SettingsItem(R.drawable.ic_language, "App language", "English (device's language"),
            SettingsItem(R.drawable.ic_help, "Help", "Help centre, contact us, privacy policy"),
            SettingsItem(R.drawable.ic_invite, "Invite a friend", null),
        )
    }

    private fun setUpRecyclerView(){
        val recyclerView = binding.settingsRecyclerView

        val settingsAdapter = SettingsAdapter(this, settingsData)
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