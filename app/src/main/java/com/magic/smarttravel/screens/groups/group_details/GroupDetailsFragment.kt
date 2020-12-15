package com.magic.smarttravel.screens.groups.group_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.profile.ProfileActivity
import com.magic.smarttravel.screens.chat.ChatActivity
import com.magic.smarttravel.screens.new_group.ContactModel
import com.magic.smarttravel.screens.new_group.ContactsAdapter
import kotlinx.android.synthetic.main.fragment_group_details.*
import kotlinx.android.synthetic.main.fragment_group_details.membersAmount
import kotlinx.android.synthetic.main.fragment_new_group.*
import kotlinx.android.synthetic.main.fragment_select_contacts.rvContacts
import org.koin.android.viewmodel.ext.android.viewModel

class GroupDetailsFragment : Fragment() {

    private lateinit var adapter: GroupDetailsAdapter
    private val groupDetailsViewModel: GroupDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_details, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupDetailsViewModel.getCurrentGroupDetails()
        groupDetailsViewModel.groupDetails.observe(this, Observer {
            tvTitle.text = it.name
            membersAmount.text = if (it.groupUsers.size > 0) {
                String.format("%d members", it.groupUsers.size)
            } else{
                "Not all group users` accepted the invitation."
            }
        })
        groupDetailsViewModel.users.observe(this, Observer {
            adapter.list = it.toMutableList()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        profile.setOnClickListener {
            activity?.let { activity -> ProfileActivity.startActivity(activity) }
        }
        btnGroupChat.setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }
    }

    private fun initAdapter() {
        rvContacts.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = GroupDetailsAdapter()
        rvContacts.adapter = adapter
    }

}
