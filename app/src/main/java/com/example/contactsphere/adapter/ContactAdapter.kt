package com.example.contactsphere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsphere.databinding.ItemContactBinding
import com.example.contactsphere.model.Contact

class ContactAdapter(
    private var fullList: List<Contact>,
    private val onItemClick: (Contact) -> Unit,
    private val onCallClick: (Contact) -> Unit,
    private val onFavoriteClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var displayedList: List<Contact> = fullList.toList()
    private var currentQuery: String = ""

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
                ivFavorite.setImageResource(com.example.contactsphere.R.drawable.ic_star_filled)
            } else {
                ivFavorite.setImageResource(com.example.contactsphere.R.drawable.ic_star_outline)
            }

            root.setOnClickListener { onItemClick(contact) }
            ivCallQuick.setOnClickListener { onCallClick(contact) }
            ivFavorite.setOnClickListener { onFavoriteClick(contact) }
        }
    }

    override fun getItemCount(): Int = displayedList.size

    fun updateList(newList: List<Contact>) {
        fullList = newList
        filter(currentQuery)
    }

    fun filter(query: String) {
        currentQuery = query
        displayedList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.role.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
