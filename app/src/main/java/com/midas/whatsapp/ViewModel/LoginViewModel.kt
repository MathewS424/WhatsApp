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

class LoginViewModel(private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()): ViewModel() {



    private val _loginResult = MutableLiveData<CustomResult<FirebaseUser>>()
    val loginResult: LiveData<CustomResult<FirebaseUser>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    private val _logoutResult = MutableLiveData<CustomResult<Unit>>()
    val logoutResult: LiveData<CustomResult<Unit>> = _logoutResult

    init{
        checkCurrentUser()
    }

    fun loginUser(email: String, password: String){

        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            _loginResult.value = result
            _isLoading.value = false
        }

    }

    fun checkCurrentUser(){
        _currentUser.value = authRepository.getCurrentUser()
    }

    fun signOut(){
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.signOut()
            _logoutResult.value = result
            _isLoading.value = false
            if(result is CustomResult.Success){
                _currentUser.value = null
            }
        }
    }



}