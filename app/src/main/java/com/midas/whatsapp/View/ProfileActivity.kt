package com.midas.whatsapp.View

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.midas.whatsapp.Model.data.SettingsItem
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.SettingsAdapter
import com.midas.whatsapp.ViewModel.LoginViewModel
import com.midas.whatsapp.ViewModel.UserProfileViewModel
import com.midas.whatsapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileData: List<SettingsItem>
    private val viewModel: UserProfileViewModel by viewModels()

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

        setUpProfileDataAndUi()
    }

    private fun setUpProfileDataAndUi() {

        viewModel.userProfile.observe(this){user ->
            if(user != null) {
                val userName = user.displayName

                profileData = listOf(
                    SettingsItem(R.drawable.ic_person, "Name", userName, "NAME"),
                    SettingsItem(R.drawable.ic_about, "About", "NEVER QUIT REPEAT", "ABOUT"),
                    SettingsItem(R.drawable.icon_call, "Phone", "+91 9074237884", "PHONE"),
                    SettingsItem(R.drawable.ic_link, "Links", "Add links", "LINKS")
                )
                setUpRecyclerView()
            }
        }
    }

    private fun setUpRecyclerView() {
        val recyclerView = binding.profileRecyclerView
        val profileAdapter = SettingsAdapter(profileData) { item ->
            when (item.actionType) {
                "NAME" -> {
                    val intent = Intent(this, ChangeNameActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = profileAdapter
    }
}