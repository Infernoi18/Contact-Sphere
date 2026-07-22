package com.example.contactsphere.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactsphere.MainActivity
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
            onCallClick = { contact ->
                (activity as? MainActivity)?.placeCall(contact.phone)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BaseContactFragment.adapter
        }

        updateEmptyState()
    }

    fun filterContacts(query: String) {
        adapter.filter(query)
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

    private fun showContactBottomSheet(contact: Contact) {
        val bottomSheet = ContactDetailsBottomSheet.newInstance(
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
