package com.midas.whatsapp.View

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.android.material.tabs.TabLayoutMediator
import com.midas.whatsapp.R
import com.midas.whatsapp.View.adapter.MainViewPagerAdapter
import com.midas.whatsapp.ViewModel.LoginViewModel
import com.midas.whatsapp.databinding.ActivityMainBinding
import com.midas.whatsapp.util.CustomResult

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    private val signInViewModel: LoginViewModel by viewModels()

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
        setupFab()
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

    private fun setupFab(){
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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        searchMenuItem = menu?.findItem(R.id.menu_search)
        val searchView = searchMenuItem?.actionView as? SearchView
        searchView?.apply {
            queryHint = "Search chats..."

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toast.makeText(this@MainActivity, "Search submitted: $query", Toast.LENGTH_SHORT).show()
                    searchMenuItem?.collapseActionView() // To collapse after search
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Toast.makeText(this@MainActivity, "Searching: $newText", Toast.LENGTH_SHORT).show()
                    return true
                }
            })

            searchMenuItem?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    mainBinding.toolbarTitle.visibility = android.view.View.GONE
                    mainBinding.tabLayout.visibility = android.view.View.GONE
                    mainBinding.viewPager2.visibility = android.view.View.GONE

                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    mainBinding.toolbarTitle.visibility = android.view.View.VISIBLE
                    mainBinding.tabLayout.visibility = android.view.View.VISIBLE
                    mainBinding.viewPager2.visibility = android.view.View.VISIBLE
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
                true
            }
            R.id.logOut -> {
                triggerLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}