package com.example.contactsphere.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsphere.R
import com.example.contactsphere.databinding.ItemContactBinding
import com.example.contactsphere.model.Contact

class ContactAdapter(
    private var fullList: List<Contact>,
    private val onItemClick: (Contact) -> Unit,
    private val onItemLongClick: (Contact) -> Unit,
    private val onCallClick: (Contact) -> Unit,
    private val onFavoriteClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var displayedList: List<Contact> = fullList.toList()
    private var currentQuery: String = ""
    private var currentSortOrder: String = "AZ"
    private var currentRoleFilter: String = "All"

    inner class ContactViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = displayedList[position]
        with(holder.binding) {
            tvName.text = contact.name
            tvRole.text = contact.role
            tvPhone.text = contact.phone
            
            if (contact.isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_star_filled)
            } else {
                ivFavorite.setImageResource(R.drawable.ic_star_outline)
            }

            root.setOnClickListener { onItemClick(contact) }
            root.setOnLongClickListener {
                onItemLongClick(contact)
                true
            }
            ivCallQuick.setOnClickListener { onCallClick(contact) }
            ivFavorite.setOnClickListener { onFavoriteClick(contact) }
        }
    }

    override fun getItemCount(): Int = displayedList.size

    fun updateList(newList: List<Contact>) {
        fullList = newList
        applySortAndFilter(currentSortOrder, currentRoleFilter)
    }

    fun applySortAndFilter(sortOrder: String, roleFilter: String) {
        currentSortOrder = sortOrder
        currentRoleFilter = roleFilter
        
        var filteredList = if (roleFilter == "All" || roleFilter == "All Roles") {
            fullList
        } else {
            fullList.filter { it.role == roleFilter }
        }

        filteredList = if (sortOrder == "AZ") {
            filteredList.sortedBy { it.name }
        } else {
            filteredList.sortedByDescending { it.name }
        }

        displayedList = if (currentQuery.isEmpty()) {
            filteredList
        } else {
            filteredList.filter {
                it.name.contains(currentQuery, ignoreCase = true) ||
                        it.role.contains(currentQuery, ignoreCase = true) ||
                        it.phone.contains(currentQuery, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        currentQuery = query
        applySortAndFilter(currentSortOrder, currentRoleFilter)
    }
}
