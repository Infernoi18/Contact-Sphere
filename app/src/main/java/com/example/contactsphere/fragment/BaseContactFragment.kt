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

abstract class BaseContactFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    protected val binding get() = _binding!!
    protected lateinit var adapter: ContactAdapter

    abstract fun getContactsList(): List<Contact>

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

        adapter = ContactAdapter(
            fullList = getContactsList(),
            onItemClick = { contact ->
                showContactBottomSheet(contact)
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

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BaseContactFragment.adapter
        }

        com.example.contactsphere.utils.DummyDataProvider.contacts.observe(viewLifecycleOwner) {
            refreshData()
        }

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

    private fun showDeleteConfirmation(contact: Contact) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.dialog_delete_message, contact.name))
            .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                com.example.contactsphere.utils.DummyDataProvider.deleteContact(requireContext(), contact.id)
                refreshData()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
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

//    private fun refreshData() {
//        adapter.updateList(getContactsList())
//        updateEmptyState()
//        (activity as? MainActivity)?.refreshContacts()
//        adapter.notifyDataSetChanged()
//        binding.recyclerView.adapter = adapter
//        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerView.adapter?.notifyDataSetChanged()
//        binding.recyclerView.layoutManager?.scrollToPosition(0)
//        updateEmptyState()
//        adapter.notifyDataSetChanged()
//        binding.recyclerView.adapter?.notifyDataSetChanged()
//        binding.recyclerView.layoutManager?.scrollToPosition(0)
//        updateEmptyState()
//
//    }


    fun refreshData() {
        adapter.updateList(getContactsList())
        updateEmptyState()
    }

    private fun showContactBottomSheet(contact: Contact) {
        val bottomSheet = ContactDetailsBottomSheet.newInstance(
            id = contact.id,
            name = contact.name,
            role = contact.role,
            phone = contact.phone,
            bio = contact.bio,
            isFavorite = contact.isFavorite
        )
        bottomSheet.show(childFragmentManager, ContactDetailsBottomSheet.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
