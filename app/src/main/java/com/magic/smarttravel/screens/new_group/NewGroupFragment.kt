package com.magic.smarttravel.screens.new_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.groups.GroupsActivity
import kotlinx.android.synthetic.main.fragment_new_group.*
import kotlinx.android.synthetic.main.fragment_select_contacts.rvContacts
import kotlinx.android.synthetic.main.fragment_select_contacts.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class NewGroupFragment : Fragment() {

    lateinit var selectedContactsList: ContactList
    val groupViewModel: NewGroupViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedContactsList = arguments?.getSerializable(SELECTED_CONTACTS) as ContactList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.toolbar.arrow.setOnClickListener {
            activity?.onBackPressed()
        }
        view.actionButton.setOnClickListener {
            val name = etGroupName.text.toString()
            if (name.isNotBlank()) {
                groupViewModel.createNewGroup(name, selectedContactsList)
            }
        }
        membersAmount.text = String.format("You and %d members", selectedContactsList.list.size)
        initAdapter(selectedContactsList.list)

        this.observeViewModel()
    }

    private fun observeViewModel() {
        groupViewModel.groupCreated().observe(viewLifecycleOwner, Observer {
            GroupsActivity.startActivity(requireContext())
            this.activity?.finish()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_group, container, false)
    }

    private fun initAdapter(list: MutableList<ContactModel>?) {
        rvContacts.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = list?.let {
            ContactsAdapter(it, false, null)
        }
        rvContacts.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewGroupFragment()
    }
}
