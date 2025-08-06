package com.midas.whatsapp.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.MainViewPagerAdapter
import com.midas.whatsapp.View.adapter.UserAdapter
import com.midas.whatsapp.ViewModel.LoginViewModel
import com.midas.whatsapp.ViewModel.UserListViewModel
import com.midas.whatsapp.databinding.ActivityMainBinding
import com.midas.whatsapp.util.CustomResult

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    private val signInViewModel: LoginViewModel by viewModels()
    private val userListViewModel: UserListViewModel by viewModels()


    private lateinit var userAdapter: UserAdapter

    private var searchMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        setSupportActionBar(mainBinding.toolbar)
        supportActionBar?.title = ""
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }


        setupViewPagerAndTabs()
        setupListeners()
        setUpRecyclerView()
        setUpObservers()



    }



    private fun setupViewPagerAndTabs() {
        val viewPager2Adapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
        mainBinding.viewPager2.adapter = viewPager2Adapter

        TabLayoutMediator(mainBinding.tabLayout, mainBinding.viewPager2) { tab, position ->
            tab.text = when(position){
                0 -> "CHATS"
                1 -> "STATUS"
                2 -> "CALLS"
                else -> "TAB"
            }
        }.attach()
    }

    private fun setupListeners(){
        mainBinding.fabShowContacts.setOnClickListener {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, UserListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun triggerLogout() {

        signInViewModel.signOut()
        val intent = Intent(this@MainActivity, SignUpActivity::class.java)
        startActivity(intent)
        finish()

    }

    private fun setUpObservers() {
        signInViewModel.logoutResult.observe(this) { result ->
            when (result) {
                is CustomResult.Success -> {
                    Toast.makeText(this@MainActivity, "Logout Successfully!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@MainActivity, SignUpActivity::class.java)
                    startActivity(intent)
                }

                is CustomResult.Failure -> {
                    Toast.makeText(
                        this@MainActivity,
                        " Logout Failed: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }

        userListViewModel.recentUsers.observe(this){ result ->
            when(result){
                is CustomResult.Success -> {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    val filteredUsers = result.data.filter { it.uid != currentUserId }.toList()

                    userAdapter.submitList(filteredUsers)

                }
                is CustomResult.Failure -> {
                    Toast.makeText(
                        this,
                        "Failed to load users: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        searchMenuItem = menu?.findItem(R.id.menu_search)
        val searchView = searchMenuItem?.actionView as? SearchView
        searchView?.apply {
            queryHint = "Search chats..."

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //searchMenuItem?.collapseActionView() // To collapse after search

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    userListViewModel.searchUsers(newText.orEmpty(), "recent_chat")
                    return true
                }
            })

            searchMenuItem?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    mainBinding.toolbarTitle.visibility = android.view.View.GONE
                    mainBinding.tabLayout.visibility = android.view.View.GONE
                    mainBinding.viewPager2.visibility = android.view.View.GONE
                    mainBinding.recyclerViewMainRecentUsers.visibility = View.VISIBLE

                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    mainBinding.toolbarTitle.visibility = android.view.View.VISIBLE
                    mainBinding.tabLayout.visibility = android.view.View.VISIBLE
                    mainBinding.viewPager2.visibility = android.view.View.VISIBLE
                    mainBinding.recyclerViewMainRecentUsers.visibility = View.GONE
                    searchView.setQuery("", false)
                    return true
                }

            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_search -> {
                true
            }
            R.id.menu_camera -> {
                Toast.makeText(this@MainActivity, "Accessing Camera", Toast.LENGTH_SHORT).show()
                cameraAccess()
                true
            }
            R.id.logOut -> {
                triggerLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setUpRecyclerView(){
        userAdapter = UserAdapter{user->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if(currentUserId == user.uid){
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
        mainBinding.recyclerViewMainRecentUsers.layoutManager = LinearLayoutManager(this)
        mainBinding.recyclerViewMainRecentUsers.adapter = userAdapter

    }

    private fun cameraAccess(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            startPermissionRequest()
        }else{
            startCamera()
        }
    }

    private fun startCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(intent)
    }

    private fun startPermissionRequest(){
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted ->
        if(isGranted){
            Toast.makeText(this@MainActivity, "Permission Allowed", Toast.LENGTH_SHORT).show()
            startCamera()
        }
        else{
            Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

}