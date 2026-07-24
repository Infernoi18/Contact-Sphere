package com.example.contactsphere.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactsphere.MainActivity
import com.example.contactsphere.R
import com.example.contactsphere.adapter.ContactAdapter
import com.example.contactsphere.bottomsheet.ContactDetailsBottomSheet
import com.example.contactsphere.databinding.FragmentContactListBinding
import com.example.contactsphere.model.Contact
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseContactFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var adapter: ContactAdapter

    abstract fun getContacts(): List<Contact>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(
            fullList = getContacts(),
            onItemClick = { contact ->
                val bottomSheet = ContactDetailsBottomSheet.newInstance(
                    contact.id,
                    contact.name,
                    contact.phone,
                    contact.role,
                    contact.bio,
                    contact.isFavorite
                )
                bottomSheet.show(parentFragmentManager, "ContactDetails")
            },
            onItemLongClick = { contact ->
                showDeleteConfirmation(contact)
            },
            onCallClick = { contact ->
                (activity as? MainActivity)?.placeCall(contact.phone)
            },
            onFavoriteClick = { contact ->
                com.example.contactsphere.utils.DummyDataProvider.toggleFavorite(requireContext(), contact.id)
                (activity as? MainActivity)?.refreshContacts()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        updateEmptyState()
    }

    private fun observeData() {
        com.example.contactsphere.utils.DummyDataProvider.contacts.observe(viewLifecycleOwner) {
            refreshData()
        }
    }

    private fun showDeleteConfirmation(contact: Contact) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.dialog_delete_message, contact.name))
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                com.example.contactsphere.utils.DummyDataProvider.deleteContact(requireContext(), contact.id)
                refreshData()
            }
            .show()
    }

    fun refreshData() {
        val updatedList = getContacts()
        adapter.updateList(updatedList)
        updateEmptyState()
    }

    fun filterContacts(query: String) {
        adapter.filter(query)
        updateEmptyState()
    }

    fun applySortAndFilter(sortOrder: String, roleFilter: String) {
        adapter.applySortAndFilter(sortOrder, roleFilter)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (adapter.itemCount == 0) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
