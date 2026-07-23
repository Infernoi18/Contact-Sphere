package com.example.contactsphere.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.contactsphere.model.Contact
import java.util.UUID

object DummyDataProvider {

    private val contactsList = mutableListOf<Contact>()

    private val _contacts = MutableLiveData<List<Contact>>(contactsList.toList())
    val contacts: LiveData<List<Contact>> = _contacts

    fun getDummyContacts(): List<Contact> {
        return contactsList.toList()
    }

    fun toggleFavorite(contactId: String) {
        val index = contactsList.indexOfFirst { it.id == contactId }
        if (index != -1) {
            val contact = contactsList[index]
            contactsList[index] = contact.copy(isFavorite = !contact.isFavorite)
            _contacts.postValue(contactsList.toList())
        }
    }

    fun addContact(name: String, phone: String, role: String, bio: String) {
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
    }

    fun deleteContact(contactId: String) {
        contactsList.removeAll { it.id == contactId }
        _contacts.postValue(contactsList.toList())
    }
}