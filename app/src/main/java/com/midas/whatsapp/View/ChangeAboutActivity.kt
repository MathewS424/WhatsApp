package com.midas.whatsapp.View

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.midas.whatsapp.R
import com.midas.whatsapp.View.fragment.NewAboutFragment
import com.midas.whatsapp.ViewModel.AboutViewModel
import com.midas.whatsapp.ViewModel.UserProfileViewModel
import com.midas.whatsapp.databinding.ActivityChangeAboutBinding

class ChangeAboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeAboutBinding

    private val viewModel: UserProfileViewModel by viewModels()
    private val aboutViewModel: AboutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangeAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpAboutData()
        setUpListeners()

    }


    private fun setUpAboutData() {
        viewModel.userProfile.observe(this) { user ->
            if (user != null) {
                val about = user.about
                binding.aboutEditView.setText(about)
            }
        }
    }

    private fun setUpListeners() {
        binding.aboutEditView.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.aboutEditView.right - binding.aboutEditView.compoundDrawables[2].bounds.width()) {
                    NewAboutFragment().show(supportFragmentManager, "changeAbout")
                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })

        aboutViewModel.aboutText.observe(this){
            binding.aboutEditView.setText(it)
        }

        binding.availableId.setOnClickListener {
            val text = binding.availableId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.busyId.setOnClickListener {
            val text = binding.busyId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.atSchoolId.setOnClickListener {
            val text = binding.atSchoolId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.atWorkId.setOnClickListener {
            val text = binding.atWorkId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.batteryId.setOnClickListener {
            val text = binding.batteryId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.talkTextId.setOnClickListener {
            val text = binding.talkTextId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.meetingId.setOnClickListener {
            val text = binding.meetingId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.movieId.setOnClickListener {
            val text = binding.movieId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.sleepingId.setOnClickListener {
            val text = binding.sleepingId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
        binding.urgentCallId.setOnClickListener {
            val text = binding.urgentCallId.text.toString()
            aboutViewModel.aboutText.value = text
            viewModel.updateAbout(text)
        }
    }
}