package com.example.contactsphere.bottomsheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contactsphere.MainActivity
import com.example.contactsphere.R
import com.example.contactsphere.databinding.BottomSheetContactDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ContactDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetContactDetailsBinding? = null
    private val binding get() = _binding!!

    private var contactId: String? = null
    private var name: String? = null
    private var phone: String? = null
    private var role: String? = null
    private var bio: String? = null
    private var isFavorite: Boolean = false

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_PHONE = "arg_phone"
        private const val ARG_ROLE = "arg_role"
        private const val ARG_BIO = "arg_bio"
        private const val ARG_FAVORITE = "arg_favorite"

        fun newInstance(
            id: String,
            name: String,
            phone: String,
            role: String,
            bio: String,
            isFavorite: Boolean
        ): ContactDetailsBottomSheet {
            val fragment = ContactDetailsBottomSheet()
            val args = Bundle().apply {
                putString(ARG_ID, id)
                putString(ARG_NAME, name)
                putString(ARG_PHONE, phone)
                putString(ARG_ROLE, role)
                putString(ARG_BIO, bio)
                putBoolean(ARG_FAVORITE, isFavorite)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId = it.getString(ARG_ID)
            name = it.getString(ARG_NAME)
            phone = it.getString(ARG_PHONE)
            role = it.getString(ARG_ROLE)
            bio = it.getString(ARG_BIO)
            isFavorite = it.getBoolean(ARG_FAVORITE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetContactDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvBsName.text = name
        binding.tvBsPhone.text = phone
        binding.tvBsRole.text = role
        binding.tvBsBio.text = bio

        updateFavoriteUI(isFavorite)

        val id = contactId ?: return

        binding.ivFavorite.setOnClickListener {
            com.example.contactsphere.utils.DummyDataProvider.toggleFavorite(requireContext(), id)
            isFavorite = !isFavorite
            updateFavoriteUI(isFavorite)
            (activity as? MainActivity)?.refreshContacts()
        }

        binding.btnCall.setOnClickListener {
            phone?.let { num ->
                (activity as? MainActivity)?.placeCall(num)
            }
        }

        binding.tvReadMore.setOnClickListener {
            if (binding.tvBsBio.maxLines == 3) {
                binding.tvBsBio.maxLines = Int.MAX_VALUE
                binding.tvReadMore.text = getString(R.string.read_less)
            } else {
                binding.tvBsBio.maxLines = 3
                binding.tvReadMore.text = getString(R.string.read_more)
            }
        }
    }

    private fun updateFavoriteUI(favorite: Boolean) {
        if (favorite) {
            binding.ivFavorite.setImageResource(R.drawable.ic_star_filled)
        } else {
            binding.ivFavorite.setImageResource(R.drawable.ic_star_outline)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
