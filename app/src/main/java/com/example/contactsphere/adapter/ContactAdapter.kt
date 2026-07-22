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
    private val onCallClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var displayedList: List<Contact> = fullList.toList()

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
            ivFavorite.visibility = if (contact.isFavorite) View.VISIBLE else View.GONE

            root.setOnClickListener { onItemClick(contact) }
            ivCallQuick.setOnClickListener { onCallClick(contact) }
        }
    }

    override fun getItemCount(): Int = displayedList.size

    fun updateList(newList: List<Contact>) {
        fullList = newList
        displayedList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
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
