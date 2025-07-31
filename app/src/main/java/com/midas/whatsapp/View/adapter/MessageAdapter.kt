package com.midas.whatsapp.View.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.midas.whatsapp.Model.data.Message
import com.midas.whatsapp.databinding.ItemMessageReceivedBinding
import com.midas.whatsapp.databinding.ItemMessageSendBinding
import java.util.Date

class MessageAdapter(private val currentUserId: String): ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SEND = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if(message.senderId == currentUserId){
            VIEW_TYPE_SEND
        }else{
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SEND){
            val binding = ItemMessageSendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        }else{
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when(holder.itemViewType){
            VIEW_TYPE_SEND -> (holder as SentMessageViewHolder).bind(message)
            VIEW_TYPE_RECEIVED -> (holder as ReceivedMessageViewHolder).bind(message)
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemMessageSendBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(message:Message){
            binding.tvMessageText.text = message.text
            binding.tvMessageTime.text = DateFormat.format("hh:mm a", Date(message.timestamp)).toString()
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
            binding.tvMessageText.text = message.text
            binding.tvMessageTime.text = DateFormat.format("hh:mm a", Date(message.timestamp)).toString()
        }
    }

    class MessageDiffCallback: DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return  oldItem == newItem
        }

    }
}