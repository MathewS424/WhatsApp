package com.midas.whatsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.Model.repository.ChatRepository
import com.midas.whatsapp.Model.repository.FirebaseChatRepositoryImpl
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListViewModel(private val chatRepository: ChatRepository = FirebaseChatRepositoryImpl()): ViewModel() {

    private val _users = MutableLiveData<CustomResult<List<User>>>()
    val users: LiveData<CustomResult<List<User>>> = _users

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchUsers()
    }

    private fun fetchUsers(){
        _isLoading.value = true
        viewModelScope.launch {
            chatRepository.getUsers().collectLatest { result ->
                _users.value = result
                _isLoading.value = false
            }
        }
    }
}