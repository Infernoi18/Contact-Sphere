package com.example.contactsphere.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.contactsphere.model.Contact
import org.json.JSONArray
import org.json.JSONObject

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "contact_sphere_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_PROFILE_URI = "profile_uri"
    }

    fun saveUser(username: String, password: String, rememberMe: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USERNAME, username)
            if (rememberMe) {
                putString(KEY_PASSWORD, password)
                putBoolean(KEY_REMEMBER_ME, true)
            } else {
                remove(KEY_PASSWORD)
                putBoolean(KEY_REMEMBER_ME, false)
            }
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""

    fun getSavedPassword(): String = prefs.getString(KEY_PASSWORD, "") ?: ""

    fun isRememberMeEnabled(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)

    fun saveProfileUri(uriString: String) {
        prefs.edit().putString(KEY_PROFILE_URI, uriString).apply()
    }

    fun getProfileUri(): String = prefs.getString(KEY_PROFILE_URI, "") ?: ""

    fun clearSession() {
        val remember = isRememberMeEnabled()
        val savedUser = getUsername()
        val savedPass = getSavedPassword()

        prefs.edit().clear().apply()

        if (remember) {
            prefs.edit().apply {
                putString(KEY_USERNAME, savedUser)
                putString(KEY_PASSWORD, savedPass)
                putBoolean(KEY_REMEMBER_ME, true)
                apply()
            }
        }
    }

    fun saveContacts(username: String, contacts: List<Contact>) {
        val jsonArray = JSONArray()
        contacts.forEach { contact ->
            val jsonObj = JSONObject()
            jsonObj.put("id", contact.id)
            jsonObj.put("name", contact.name)
            jsonObj.put("phone", contact.phone)
            jsonObj.put("role", contact.role)
            jsonObj.put("bio", contact.bio)
            jsonObj.put("isFavorite", contact.isFavorite)
            jsonArray.put(jsonObj)
        }
        prefs.edit().putString("contacts_$username", jsonArray.toString()).apply()
    }

    fun getContacts(username: String): List<Contact> {
        val jsonString = prefs.getString("contacts_$username", null) ?: return emptyList()
        val contacts = mutableListOf<Contact>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)
                contacts.add(
                    Contact(
                        id = jsonObj.getString("id"),
                        name = jsonObj.getString("name"),
                        phone = jsonObj.getString("phone"),
                        role = jsonObj.getString("role"),
                        bio = jsonObj.getString("bio"),
                        isFavorite = jsonObj.getBoolean("isFavorite")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contacts
    }
}
