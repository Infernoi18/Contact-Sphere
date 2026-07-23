package com.example.contactsphere.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.contactsphere.model.Contact
import java.util.UUID

object DummyDataProvider {

    private val contactsList = mutableListOf<Contact>()
    private var currentUsername: String? = null

    private val _contacts = MutableLiveData<List<Contact>>(contactsList.toList())
    val contacts: LiveData<List<Contact>> = _contacts

    fun loadContacts(context: Context, username: String) {
        this.currentUsername = username
        val prefsManager = PrefsManager(context)
        val savedContacts = prefsManager.getContacts(username)
        
        contactsList.clear()
        contactsList.addAll(savedContacts)
        _contacts.postValue(contactsList.toList())
    }

    private fun persist(context: Context) {
        currentUsername?.let { username ->
            PrefsManager(context).saveContacts(username, contactsList.toList())
        }
    }

    fun getDummyContacts(): List<Contact> {
        return contactsList.toList()
    }

    fun toggleFavorite(context: Context, contactId: String) {
        val index = contactsList.indexOfFirst { it.id == contactId }
        if (index != -1) {
            val contact = contactsList[index]
            contactsList[index] = contact.copy(isFavorite = !contact.isFavorite)
            _contacts.postValue(contactsList.toList())
            persist(context)
        }
    }

    fun addContact(context: Context, name: String, phone: String, role: String, bio: String) {
        val newContact = Contact(
            id = UUID.randomUUID().toString(),
            name = name,
            phone = phone,
            role = role,
            bio = bio,
            isFavorite = false
        )
        contactsList.add(newContact)
        _contacts.postValue(contactsList.toList())
        persist(context)
    }

    fun deleteContact(context: Context, contactId: String) {
        contactsList.removeAll { it.id == contactId }
        _contacts.postValue(contactsList.toList())
        persist(context)
    }
}
