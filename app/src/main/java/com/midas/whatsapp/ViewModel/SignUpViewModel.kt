package com.midas.whatsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.midas.whatsapp.Model.repository.AuthRepository
import com.midas.whatsapp.Model.repository.FirebaseAuthRepositoryImpl
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()): ViewModel() {



    // LiveData to observe the registration status
    // MutableLiveData is used internally in the ViewModel to post updates
    // LiveData is exposed to the View for observation
    private val _registrationResult = MutableLiveData<CustomResult<FirebaseUser>>()
    val registrationResult: LiveData<CustomResult<FirebaseUser>> = _registrationResult

    // LiveData to observe loading state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading



    /**
     * Register a new user with email and password
     */
    fun registerUser(email: String, password: String){
        _isLoading.value = true

        viewModelScope.launch {
            val result = authRepository.registerUser(email, password)
            _registrationResult.value = result
            _isLoading.value = false
        }
    }
}

