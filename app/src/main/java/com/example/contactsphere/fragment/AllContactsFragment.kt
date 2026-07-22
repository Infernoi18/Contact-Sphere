package com.example.contactsphere.fragment

import com.example.contactsphere.model.Contact
import com.example.contactsphere.utils.DummyDataProvider

class AllContactsFragment : BaseContactFragment() {

    override fun getContactsList(): List<Contact> {
        return DummyDataProvider.getDummyContacts()
    }
}
