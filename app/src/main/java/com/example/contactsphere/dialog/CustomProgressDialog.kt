package com.example.contactsphere.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.example.contactsphere.R
import com.example.contactsphere.databinding.DialogCustomProgressBinding

class CustomProgressDialog(
    context: Context,
    private val message: String? = null
) : Dialog(context, R.style.Theme_ContactSphere_Dialog) {

    private lateinit var binding: DialogCustomProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogCustomProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCancelable(false)
        binding.tvProgressMessage.text = message ?: context.getString(R.string.loading)
    }
}
