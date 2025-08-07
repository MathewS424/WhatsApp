package com.midas.whatsapp.View.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.midas.whatsapp.View.ChatActivity
import com.midas.whatsapp.View.adapter.UserAdapter
import com.midas.whatsapp.ViewModel.UserListViewModel
import com.midas.whatsapp.databinding.FragmentChatsBinding
import com.midas.whatsapp.util.CustomResult


class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter

    private val userListViewModel: UserListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        setUpObservers()
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView(){
        userAdapter = UserAdapter{user->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if(currentUserId == user.uid){
                Toast.makeText(
                    requireContext(),
                    "You cannot chat with yourself. Please select another user.",
                    Toast.LENGTH_SHORT
                ).show()
                return@UserAdapter
            }
            userListViewModel.resetMessageCount(user.uid)

            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra("otherUserId", user.uid)
                putExtra("otherUserName", user.displayName ?: user.email)
            }
            startActivity(intent)
        }
        _binding?.recyclerViewRecentUsers?.layoutManager = LinearLayoutManager(requireContext())
        _binding?.recyclerViewRecentUsers?.adapter = userAdapter


    }

    private fun  setUpObservers(){
        userListViewModel.recentUsers.observe(viewLifecycleOwner){ result ->
            when(result){
                is CustomResult.Success -> {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    val filteredUsers = result.data.filter { it.uid != currentUserId }.toList()

                    userAdapter.submitList(filteredUsers)

                }
                is CustomResult.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load users: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        userListViewModel.messageCount.observe(viewLifecycleOwner){counts ->
            userAdapter.setMessageCounts(counts)
        }
    }

}