package com.example.contactsphere.bottomsheet

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

    private var isExpanded = false

    companion object {
        const val TAG = "ContactDetailsBottomSheet"
        private const val ARG_NAME = "arg_name"
        private const val ARG_ROLE = "arg_role"
        private const val ARG_PHONE = "arg_phone"
        private const val ARG_BIO = "arg_bio"
        private const val ARG_IS_FAVORITE = "arg_is_favorite"

        fun newInstance(
            name: String,
            role: String,
            phone: String,
            bio: String,
            isFavorite: Boolean
        ): ContactDetailsBottomSheet {
            val fragment = ContactDetailsBottomSheet()
            val args = Bundle().apply {
                putString(ARG_NAME, name)
                putString(ARG_ROLE, role)
                putString(ARG_PHONE, phone)
                putString(ARG_BIO, bio)
                putBoolean(ARG_IS_FAVORITE, isFavorite)
            }
            fragment.arguments = args
            return fragment
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

        val name = arguments?.getString(ARG_NAME) ?: ""
        val role = arguments?.getString(ARG_ROLE) ?: ""
        val phone = arguments?.getString(ARG_PHONE) ?: ""
        val bio = arguments?.getString(ARG_BIO) ?: ""
        val isFavorite = arguments?.getBoolean(ARG_IS_FAVORITE) ?: false

        binding.tvBsName.text = name
        binding.tvBsRole.text = role
        binding.tvBsPhone.text = phone
        binding.tvBsBio.text = bio

        if (isFavorite) {
            binding.ivFavorite.setImageResource(R.drawable.ic_star_filled)
        } else {
            binding.ivFavorite.setImageResource(R.drawable.ic_star_outline)
        }

        binding.tvReadMore.setOnClickListener {
            if (isExpanded) {
                binding.tvBsBio.maxLines = 2
                binding.tvReadMore.text = getString(R.string.read_more)
                isExpanded = false
            } else {
                binding.tvBsBio.maxLines = Int.MAX_VALUE
                binding.tvReadMore.text = getString(R.string.read_less)
                isExpanded = true
            }
        }

        binding.btnCall.setOnClickListener {
            dismiss()
            (activity as? MainActivity)?.placeCall(phone)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
