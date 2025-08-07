package com.midas.whatsapp.View.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.midas.whatsapp.databinding.ItemUserBinding
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.ViewModel.UserListViewModel

class UserAdapter(private val onItemClicked: (User) -> Unit) :
    ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    private var messageCounts: Map<String, Int> = emptyMap()

    fun setMessageCounts(newCounts: Map<String, Int>){
        this.messageCounts = newCounts
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        val user = getItem(position)

        val messageCount = messageCounts[user.uid] ?: 0
        holder.bind(user, messageCount)


    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(adapterPosition))
                }
            }
        }

        fun bind(user: User, messageCount: Int) {
            binding.tvUserName.text = user.displayName ?: user.email?.split("@")?.get(0)
            binding.tvUserEmail.text = user.email
            Log.d("messages", "Bind Value: $messageCount")
            if(messageCount > 0){
                binding.messageCount.visibility = View.VISIBLE
                binding.messageCount.text = messageCount.toString()
            }else{
                binding.messageCount.visibility = View.GONE
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
}