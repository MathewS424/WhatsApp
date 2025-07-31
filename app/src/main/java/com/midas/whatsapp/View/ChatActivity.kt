package com.midas.whatsapp.View

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.MessageAdapter
import com.midas.whatsapp.ViewModel.ChatViewModel
import com.midas.whatsapp.databinding.ActivityChatBinding
import com.midas.whatsapp.databinding.ItemMessageReceivedBinding
import com.midas.whatsapp.util.CustomResult

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModels()

    private lateinit var messageAdapter: MessageAdapter
    private var otherUserId: String? = null
    private var otherUserName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatActivityMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otherUserId = intent.getStringExtra("otherUserId")
        otherUserName = intent.getStringExtra("otherUserName")

        if(otherUserId == null){
            Toast.makeText(this@ChatActivity, "Error: No user to chat with.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up the toolbar for the chat screen
        setSupportActionBar(binding.chatToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.tvOtherUserName.text = otherUserName

        setUpRecyclerView()
        setUpListeners()
        setUpObservers()

        otherUserId?.let{chatViewModel.initializeChat(it)}

    }

    //  Handle toolbar back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView(){
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if(currentUserId == null){
            Toast.makeText(this@ChatActivity, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        messageAdapter = MessageAdapter(currentUserId)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.layoutManager = layoutManager
        binding.recyclerViewMessages.adapter = messageAdapter
    }

    private fun setUpListeners(){
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            if (messageText.isNotBlank()) { // Add a check to prevent sending empty messages
                otherUserId?.let { receiverId ->
                    chatViewModel.sendMessage(messageText, receiverId)
                    binding.etMessage.text.clear() // Clear the input field immediately after sending
                }
            } else {
                Toast.makeText(this@ChatActivity, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpObservers(){
        chatViewModel.messages.observe(this){ result ->
            when(result){
                is CustomResult.Success -> {
                    messageAdapter.submitList(result.data){
                        if(messageAdapter.itemCount > 0){
                            binding.recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
                        }
                    }
                }
                is CustomResult.Failure -> {
                    Toast.makeText(this@ChatActivity, "Failed to load messages: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        chatViewModel.isLoading.observe(this){isLoading->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSend.isEnabled = !isLoading
            binding.etMessage.isEnabled = !isLoading
        }
    }
}