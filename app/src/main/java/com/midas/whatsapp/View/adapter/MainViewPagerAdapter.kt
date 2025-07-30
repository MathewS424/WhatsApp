package com.midas.whatsapp.View.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.midas.whatsapp.View.fragment.CallsFragment
import com.midas.whatsapp.View.fragment.ChatsFragment
import com.midas.whatsapp.View.fragment.StatusFragment

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3

    }

    override fun createFragment(position: Int): Fragment {
         return when(position){
             0 -> ChatsFragment()
             1 -> StatusFragment()
             2 -> CallsFragment()
             else -> ChatsFragment()
         }
    }
}