package com.midas.whatsapp.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.MessageAdapter
import com.midas.whatsapp.ViewModel.ChatViewModel
import com.midas.whatsapp.ViewModel.UserListViewModel
import com.midas.whatsapp.databinding.ActivityChatBinding

import com.midas.whatsapp.util.CustomResult


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    private var otherUserId: String? = null
    private var otherUserName: String? = null
    private var isSendMode = false
    private val userListViewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatActivityMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        otherUserId = intent.getStringExtra("otherUserId")
        otherUserName = intent.getStringExtra("otherUserName")

        if (otherUserId == null) {
            Toast.makeText(this@ChatActivity, "Error: No user to chat with.", Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }
        // Set up the toolbar for the chat screen
        setSupportActionBar(binding.chatToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.chatToolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.title = ""
        binding.tvOtherUserName.text = otherUserName

        setUpRecyclerView()
        setUpListeners()
        setUpObservers()

        otherUserId?.let { chatViewModel.initializeChat(it) }
    }
    //  Handle toolbar back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            userListViewModel.resetMessageCount(otherUserId.toString())
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(
                this@ChatActivity,
                "Authentication error. Please log in again.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }
        messageAdapter = MessageAdapter(currentUserId)
        val layoutManager = LinearLayoutManager(this)
        with(binding){
            recyclerViewMessages.layoutManager = layoutManager
            recyclerViewMessages.adapter = messageAdapter
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpListeners() {
        binding.btnSend.setOnClickListener {
            switchToMicIcon()
            disableEmojiPickerVisibility()
            val messageText = binding.etMessage.text.toString()
            if (messageText.isNotBlank()) { // Add a check to prevent sending empty messages
                otherUserId?.let { receiverId ->
                    chatViewModel.sendMessage(messageText, receiverId)
                    binding.etMessage.text.clear() // Clear the input field immediately after sending
                }
            } else {
                Toast.makeText(this@ChatActivity, "Message cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.etMessage.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val editText = v as EditText
                // Check if drawableStart is set
                val drawableStart = editText.compoundDrawables[0]
                if (drawableStart != null) {
                    // Get drawable bounds width
                    val drawableWidth = drawableStart.bounds.width()

                    // Check touch X is within drawableStart bounds (on the left)
                    // event.x gives touch position relative to view left
                    if (event.x <= editText.paddingStart + drawableWidth) {
                        // Drawable start clicked
                        emojiWindow()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //  No Action
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNotEmpty = !s.isNullOrBlank()
                if (isNotEmpty && !isSendMode) {
                    switchToSendIcon()
                } else if (!isNotEmpty && isSendMode) {
                    switchToMicIcon()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action
            }
        })

        binding.etMessage.setOnClickListener {
            disableEmojiPickerVisibility()
            changeEditTextDrawableStartToEmojiIconInsert()
        }
    }

    private fun setUpObservers() {
        chatViewModel.messages.observe(this) { result ->
            when (result) {
                is CustomResult.Success -> {
                    messageAdapter.submitList(result.data) {
                        if (messageAdapter.itemCount > 0) {
                            binding.recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
                        }
                    }
                }

                is CustomResult.Failure -> {
                    Toast.makeText(
                        this@ChatActivity,
                        "Failed to load messages: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        chatViewModel.isLoading.observe(this) { isLoading ->
            with(binding){
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                btnSend.isEnabled = !isLoading
                etMessage.isEnabled = !isLoading
            }
        }
    }

    private fun switchToSendIcon() {
        binding.btnSend.setImageResource(R.drawable.ic_send)
        isSendMode = true
    }

    private fun switchToMicIcon() {
        binding.btnSend.setImageResource(R.drawable.icon_mic)
        isSendMode = false
    }

    private fun emojiWindow(){
        if (binding.emojiPicker.visibility == View.VISIBLE) {
            disableEmojiPickerVisibility()
            changeEditTextDrawableStartToEmojiIconInsert()
        } else {
            enableEmojiPickerVisibility()
            changeEditTextDrawableStartToIconKeyboard()
            binding.emojiPicker.setOnEmojiPickedListener {
                binding.etMessage.append(it.emoji)
            }
        }
    }

    private fun changeEditTextDrawableStartToEmojiIconInsert(){
        binding.etMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_insert_emoticon, 0, R.drawable.icon_attach_file, 0)
    }

    private fun changeEditTextDrawableStartToIconKeyboard(){
        binding.etMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_keyboard, 0, R.drawable.icon_attach_file, 0)
    }

    private fun enableEmojiPickerVisibility(){
        binding.emojiPicker.visibility = View.VISIBLE
    }

    private fun disableEmojiPickerVisibility(){
        binding.emojiPicker.visibility = View.GONE
    }
}