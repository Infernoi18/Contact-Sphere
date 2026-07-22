package com.example.contactsphere.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.example.contactsphere.R
import com.example.contactsphere.databinding.DialogCustomAlertBinding

class CustomAlertDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val confirmText: String? = null,
    private val onConfirm: () -> Unit
) : Dialog(context, R.style.Theme_ContactSphere_Dialog) {

    private lateinit var binding: DialogCustomAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogCustomAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDialogTitle.text = title
        binding.tvDialogMessage.text = message
        binding.btnConfirm.text = confirmText ?: context.getString(R.string.btn_confirm)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }
    }
}
