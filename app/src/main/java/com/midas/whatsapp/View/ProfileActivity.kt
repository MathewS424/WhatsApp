package com.midas.whatsapp.View

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.midas.whatsapp.Model.data.SettingsItem
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.SettingsAdapter
import com.midas.whatsapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileData: List<SettingsItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpData()
        setUpRecyclerView()
    }

    private fun setUpData(){
        profileData = listOf(
            SettingsItem(R.drawable.ic_person, "Name", "Mathews Jose"),
            SettingsItem(R.drawable.ic_about, "About", "NEVER QUIT REPEAT"),
            SettingsItem(R.drawable.icon_call, "Phone", "+91 9074237884"),
            SettingsItem(R.drawable.ic_link, "Links", "Add links")
            )
    }

    private fun setUpRecyclerView(){
        val recyclerView = binding.profileRecyclerView
        val profileAdapter = SettingsAdapter(this, profileData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = profileAdapter
    }
}