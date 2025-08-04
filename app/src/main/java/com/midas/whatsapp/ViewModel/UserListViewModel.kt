package com.midas.whatsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.Model.repository.ChatRepository
import com.midas.whatsapp.Model.repository.FirebaseChatRepositoryImpl
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListViewModel(private val chatRepository: ChatRepository = FirebaseChatRepositoryImpl()): ViewModel() {

    private var allUsers: List<User> = emptyList()

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
                when(result){
                    is CustomResult.Success -> {
                        allUsers = result.data
                        _users.value = CustomResult.Success(allUsers)
                    }
                    is CustomResult.Failure -> {
                        _users.value = CustomResult.Failure(result.exception)
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun searchUsers(query: String){
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if(currentUserId == null){
            _users.value = CustomResult.Failure(Exception("Authentication error during search."))
            return
        }
        if(query.isBlank()){
            _users.value = CustomResult.Success(allUsers.filter { it.uid != currentUserId })
        }else{
            val filteredList = allUsers.filter { user ->
                user.uid != currentUserId &&
                        (user.displayName?.contains(query, ignoreCase = true) == true || user.email?.contains(query, ignoreCase = true) == true)
            }
            _users.value = CustomResult.Success(filteredList)
        }
    }
}