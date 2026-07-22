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
import com.example.contactsphere.dialog.CustomAlertDialog
import com.example.contactsphere.dialog.CustomProgressDialog
import com.example.contactsphere.fragment.BaseContactFragment
import com.example.contactsphere.utils.PermissionUtils
import com.example.contactsphere.utils.PrefsManager
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PrefsManager
    private var pendingPhoneCall: String? = null

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

        setupViewPager()
        simulateDataLoading()
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
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
