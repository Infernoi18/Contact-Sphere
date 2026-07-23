package com.example.contactsphere

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.contactsphere.adapter.ViewPagerAdapter
import com.example.contactsphere.databinding.ActivityMainBinding
import com.example.contactsphere.databinding.DialogAddContactBinding
import com.example.contactsphere.dialog.CustomAlertDialog
import com.example.contactsphere.dialog.CustomProgressDialog
import com.example.contactsphere.fragment.BaseContactFragment
import com.example.contactsphere.utils.DummyDataProvider
import com.example.contactsphere.utils.PermissionUtils
import com.example.contactsphere.utils.PrefsManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PrefsManager
    private var pendingPhoneCall: String? = null

    private var currentSortOrder = "AZ"
    private var currentRoleFilter = "All"

    private val requestCallPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingPhoneCall?.let { makeCallIntent(it) }
            } else {
                Toast.makeText(this, getString(R.string.error_call_permission), Toast.LENGTH_SHORT).show()
            }
            pendingPhoneCall = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        setSupportActionBar(binding.toolbar)

        val username = intent.getStringExtra("EXTRA_USERNAME") ?: prefsManager.getUsername()
        supportActionBar?.subtitle = getString(R.string.logged_in_as, username)

        DummyDataProvider.loadContacts(this, username)

        setupViewPager()
        simulateDataLoading()

        binding.fabAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun showAddContactDialog() {
        val dialogBinding = DialogAddContactBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etName.text.toString().trim()
            val phone = dialogBinding.etPhone.text.toString().trim()
            val role = dialogBinding.etRole.text.toString().trim()
            val bio = dialogBinding.etBio.text.toString().trim()

            if (name.isNotEmpty() && phone.isNotEmpty() && role.isNotEmpty() && bio.isNotEmpty()) {
                DummyDataProvider.addContact(this, name, phone, role, bio)
                Toast.makeText(this, getString(R.string.contact_added), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_all)
                1 -> getString(R.string.tab_favorites)
                else -> "Tab"
            }
        }.attach()
    }

    private fun simulateDataLoading() {
        val progressDialog = CustomProgressDialog(this, getString(R.string.loading_directory))
        progressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                progressDialog.dismiss()
            }
        }, 1200)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCurrentFragment(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCurrentFragment(newText ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            R.id.action_sort -> {
                showSortDialog()
                true
            }
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        val roles = DummyDataProvider.getDummyContacts().map { it.role }.distinct().toMutableList()
        roles.add(0, getString(R.string.filter_none))
        
        val items = roles.toTypedArray()
        var checkedItem = roles.indexOf(currentRoleFilter)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.menu_filter))
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                currentRoleFilter = items[which]
                applySortAndFilter()
                dialog.dismiss()
            }
            .show()
    }

    private fun showSortDialog() {
        val options = arrayOf(getString(R.string.sort_az), getString(R.string.sort_za))
        var checkedItem = if (currentSortOrder == "AZ") 0 else 1

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.menu_sort))
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                currentSortOrder = if (which == 0) "AZ" else "ZA"
                applySortAndFilter()
                dialog.dismiss()
            }
            .show()
    }

    private fun applySortAndFilter() {
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is BaseContactFragment) {
                fragment.applySortAndFilter(currentSortOrder, currentRoleFilter)
            }
        }
    }

    private fun filterCurrentFragment(query: String) {
        val currentFragmentTag = "f${binding.viewPager.currentItem}"
        val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag) as? BaseContactFragment
        fragment?.filterContacts(query)
    }

    private fun showLogoutConfirmation() {
        CustomAlertDialog(
            context = this,
            title = getString(R.string.dialog_logout_title),
            message = getString(R.string.dialog_logout_message),
            confirmText = getString(R.string.menu_logout),
            onConfirm = {
                prefsManager.clearSession()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        ).show()
    }

    fun refreshContacts() {
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is BaseContactFragment) {
                fragment.refreshData()
            }
        }
    }

    fun placeCall(phoneNumber: String) {
        pendingPhoneCall = phoneNumber
        if (PermissionUtils.hasCallPermission(this)) {
            makeCallIntent(phoneNumber)
        } else {
            requestCallPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
        }
    }

    private fun makeCallIntent(phoneNumber: String) {
        val cleanNumber = phoneNumber.replace("[^0-9+]".toRegex(), "")
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$cleanNumber"))
        try {
            startActivity(intent)
        } catch (e: SecurityException) {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
            startActivity(dialIntent)
        }
    }
}
