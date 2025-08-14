package com.midas.whatsapp.View.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.midas.whatsapp.R
import com.midas.whatsapp.ViewModel.AboutViewModel
import com.midas.whatsapp.ViewModel.UserProfileViewModel
import com.midas.whatsapp.databinding.FragmentNewAboutBinding


class NewAboutFragment : BottomSheetDialogFragment() {


    private lateinit var binding: FragmentNewAboutBinding
    private lateinit var aboutViewModel: AboutViewModel
    private  val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireActivity()
        aboutViewModel = ViewModelProvider(activity).get(AboutViewModel::class.java)
        //userProfileViewModel = ViewModelProvider(activity).get(UserProfileViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
    }


    private fun setUpListeners(){
        userProfileViewModel.userProfile.observe(requireActivity()) { user ->
            binding.newAboutEditText.setText(user?.about ?: "")
        }
        binding.saveButton.setOnClickListener {
            saveAboutTextData()
        }
    }

    private fun saveAboutTextData(){
        val aboutText = binding.newAboutEditText.text.toString()
        aboutViewModel.aboutText.value = aboutText
        userProfileViewModel.updateAbout(aboutText)
        binding.newAboutEditText.setText("")
        dismiss()
    }

}