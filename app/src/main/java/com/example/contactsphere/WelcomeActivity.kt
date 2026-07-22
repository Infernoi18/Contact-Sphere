package com.example.contactsphere

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.contactsphere.databinding.ActivityWelcomeBinding
import com.example.contactsphere.utils.PermissionUtils
import com.example.contactsphere.utils.PrefsManager
import java.io.File

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var prefsManager: PrefsManager
    private var username: String = ""
    private var tempCameraUri: Uri? = null

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(this, getString(R.string.error_camera_permission), Toast.LENGTH_SHORT).show()
            }
        }

    private val requestGalleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchGallery()
            } else {
                Toast.makeText(this, getString(R.string.error_gallery_permission), Toast.LENGTH_SHORT).show()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            val uri = tempCameraUri
            if (success && uri != null) {
                binding.ivProfilePic.setImageURI(uri)
                prefsManager.saveProfileUri(uri.toString())
                Toast.makeText(this, getString(R.string.toast_profile_updated), Toast.LENGTH_SHORT).show()
            }
        }

    private val pickGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.ivProfilePic.setImageURI(it)
                prefsManager.saveProfileUri(it.toString())
                Toast.makeText(this, getString(R.string.toast_profile_updated), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        username = intent.getStringExtra("EXTRA_USERNAME") ?: prefsManager.getUsername()

        binding.tvWelcomeUser.text = getString(R.string.welcome_title, username)

        val savedUri = prefsManager.getProfileUri()
        if (savedUri.isNotEmpty()) {
            binding.ivProfilePic.setImageURI(Uri.parse(savedUri))
        }

        binding.btnCamera.setOnClickListener {
            if (PermissionUtils.hasCameraPermission(this)) {
                launchCamera()
            } else {
                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }

        binding.btnGallery.setOnClickListener {
            if (PermissionUtils.hasGalleryPermission(this)) {
                launchGallery()
            } else {
                requestGalleryPermissionLauncher.launch(PermissionUtils.getGalleryPermissionString())
            }
        }

        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("EXTRA_USERNAME", username)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun launchCamera() {
        val photoFile = File(getExternalFilesDir("Pictures"), "profile_photo_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        tempCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    private fun launchGallery() {
        pickGalleryLauncher.launch("image/*")
    }
}
