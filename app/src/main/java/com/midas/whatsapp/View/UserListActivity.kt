package com.midas.whatsapp.View

import android.content.Intent
import android.os.Bundle
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
import com.midas.whatsapp.View.adapter.UserAdapter
import com.midas.whatsapp.ViewModel.UserListViewModel
import com.midas.whatsapp.databinding.ActivityUserListBinding
import com.midas.whatsapp.util.CustomResult

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private val userListViewModel: UserListViewModel by viewModels()

    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userListMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpRecyclerView()
        setUpObservers()
    }

    private fun setUpRecyclerView() {
        userAdapter = UserAdapter { user ->

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId == user.uid) {
                Toast.makeText(
                    this,
                    "You cannot chat with yourself. Please select another user.",
                    Toast.LENGTH_SHORT
                ).show()
                return@UserAdapter
            }

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("otherUserId", user.uid)
                putExtra("otherUserName", user.displayName ?: user.email)
            }
            startActivity(intent)
        }

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter = userAdapter

    }

    private fun setUpObservers() {
        userListViewModel.users.observe(this) { result ->
            when(result){
                is CustomResult.Success -> {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    val filteredUsers = result.data.filter { it.uid != currentUserId }
                    userAdapter.submitList(filteredUsers)
                }
                is CustomResult.Failure -> {
                    Toast.makeText(this@UserListActivity, "Failed to load users: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        userListViewModel.isLoading.observe(this) {isLoading ->
            binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
        }
    }
}