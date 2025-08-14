package com.midas.whatsapp.View

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.UserAdapter
import com.midas.whatsapp.ViewModel.UserListViewModel
import com.midas.whatsapp.databinding.ActivityUserListBinding
import com.midas.whatsapp.util.CustomResult
import com.midas.whatsapp.util.ViewExtension.gone
import com.midas.whatsapp.util.ViewExtension.show

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private val userListViewModel: UserListViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter
    private var searchMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userListMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.userListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.userListToolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.title = ""

        setUpRecyclerView()
        setUpObservers()
    }

    //  Handle toolbar back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user_list, menu)
        searchMenuItem = menu?.findItem(R.id.menu_user_list_search)
        val searchView = searchMenuItem?.actionView as? SearchView
        searchView?.apply {
            queryHint = "Search users..."

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
//                    Toast.makeText(
//                        this@UserListActivity,
//                        "Search Submitted: ${query}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    searchMenuItem?.collapseActionView()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    userListViewModel.searchUsers(newText.orEmpty(), "user_list")
                    return true
                }
            })
            searchMenuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    with(binding){
                        toolbarTitle.gone()
                        toolbarSubtitle.gone()
                    }
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    with(binding){
                        toolbarTitle.show()
                        toolbarSubtitle.show()
                    }
                    searchView.setQuery("", false)
                    return true
                }

            })
        }

        return true
    }

    private fun setUpRecyclerView() {
        userAdapter = UserAdapter { user ->

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            userListViewModel.resetMessageCount(user.uid)
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
        with(binding){
            recyclerViewUsers.layoutManager = LinearLayoutManager(this@UserListActivity)
            recyclerViewUsers.adapter = userAdapter
        }
    }

    private fun setUpObservers() {
        userListViewModel.users.observe(this) { result ->
            when (result) {
                is CustomResult.Success -> {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    val filteredUsers = result.data.filter { it.uid != currentUserId }
                    userAdapter.submitList(filteredUsers)
                }

                is CustomResult.Failure -> {
                    Toast.makeText(
                        this@UserListActivity,
                        "Failed to load users: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        userListViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}