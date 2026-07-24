package com.example.contactsphere.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.contactsphere.fragment.AllContactsFragment
import com.example.contactsphere.fragment.FavoriteContactsFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllContactsFragment()
            1 -> FavoriteContactsFragment()
            else -> AllContactsFragment()
        }
    }
}