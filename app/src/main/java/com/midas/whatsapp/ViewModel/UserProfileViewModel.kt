package com.midas.whatsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.Model.repository.UpdateUserProfile
import com.midas.whatsapp.Model.repository.UpdateUserProfileImpl
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.launch

class UserProfileViewModel(private val updateUserProfile: UpdateUserProfile = UpdateUserProfileImpl()): ViewModel() {

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> = _userProfile

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile(){
        viewModelScope.launch {
            updateUserProfile.getUserProfile().collect{ result ->
                if(result is CustomResult.Success){
                    _userProfile.postValue(result.data)
                }
            }
        }
    }

    fun updateUserName(newName: String){
        val currentUser = _userProfile.value
        if(currentUser != null){
            val updatedUser = currentUser.copy(displayName = newName)
            viewModelScope.launch {
                updateUserProfile.updateUserProfile(updatedUser, "USERNAME")
            }
        }
    }

    fun updateAbout(newAbout: String){
        val currentUser = _userProfile.value
        if(currentUser != null){
            val updatedUser = currentUser.copy(about = newAbout)
            viewModelScope.launch {
                updateUserProfile.updateUserProfile(updatedUser, "ABOUT")
            }
        }
    }
}