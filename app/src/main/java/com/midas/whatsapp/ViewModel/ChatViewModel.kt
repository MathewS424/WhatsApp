package com.midas.whatsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midas.whatsapp.Model.data.Message
import com.midas.whatsapp.Model.repository.AuthRepository
import com.midas.whatsapp.Model.repository.ChatRepository
import com.midas.whatsapp.Model.repository.FirebaseAuthRepositoryImpl
import com.midas.whatsapp.Model.repository.FirebaseChatRepositoryImpl
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository = FirebaseChatRepositoryImpl(),
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
) : ViewModel() {

    private val _messages = MutableLiveData<CustomResult<List<Message>>>()
    val messages: LiveData<CustomResult<List<Message>>> = _messages

    private val _sendMessageResult = MutableLiveData<CustomResult<Unit>>()
    val sendMessageResult: LiveData<CustomResult<Unit>> = _sendMessageResult

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentChatRoomId: String? = null

    private val currentUserId: String? get() = authRepository.getCurrentUser()?.uid

    fun initializeChat(otherUserId: String) {
        val senderId = currentUserId ?: run {
            _messages.value = CustomResult.failure(Exception("User not authenticated."))
            return
        }
        currentChatRoomId = chatRepository.getChatRoomId(senderId, otherUserId)
        fetchMessages()
    }

    private fun fetchMessages() {
        currentChatRoomId?.let { chatRoomId ->
            _isLoading.value = true
            viewModelScope.launch {
                chatRepository.getMessages(chatRoomId).collectLatest { result ->
                    _messages.value = result
                    _isLoading.value = false
                }
            }
        } ?: run {
            _messages.value = CustomResult.failure(Exception("Chat room not initialized."))
        }
    }

    fun sendMessage(text: String, receiverId: String) {
        val senderId = currentUserId ?: run {
            _sendMessageResult.value = CustomResult.failure(Exception("User not authenticated"))
            return
        }
        if (text.isBlank()) {
            _sendMessageResult.value = CustomResult.failure(Exception("Message cannot be empty"))
            return
        }

        _isLoading.value = true
        val message = Message(
            senderId = senderId,
            receiverId = receiverId,
            text = text.trim(),
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val result = chatRepository.sendMessage(message)
            _sendMessageResult.value = result
            _isLoading.value = false

        }
    }
}