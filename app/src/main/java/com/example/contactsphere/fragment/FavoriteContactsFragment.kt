package com.example.contactsphere.fragment

import com.example.contactsphere.model.Contact
import com.example.contactsphere.utils.DummyDataProvider

class FavoriteContactsFragment : BaseContactFragment() {

    override fun getContactsList(): List<Contact> {
        return DummyDataProvider.getDummyContacts().filter { it.isFavorite }
    }
}
