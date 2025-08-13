package com.midas.whatsapp.View.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.midas.whatsapp.Model.data.SettingsItem
import com.midas.whatsapp.databinding.ItemSettingsListBinding

class SettingsAdapter(private val settingsList: List<SettingsItem>, private val onItemClicked: (SettingsItem) -> Unit) :
    RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    inner class SettingsViewHolder(private val binding: ItemSettingsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    if(adapterPosition != RecyclerView.NO_POSITION){
                        onItemClicked(settingsList[adapterPosition])
                    }
                }
            }
        fun bind(item: SettingsItem) {
            binding.iconView.setImageResource(item.iconResId)

            binding.titleView.text = item.title

            if (item.subtitle != null) {
                binding.subtitleView.text = item.subtitle
                binding.subtitleView.visibility = View.VISIBLE
            } else {
                binding.subtitleView.visibility = View.GONE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {

        val binding =
            ItemSettingsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SettingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = settingsList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }


}