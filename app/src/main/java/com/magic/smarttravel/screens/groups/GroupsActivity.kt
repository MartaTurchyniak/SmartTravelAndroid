package com.magic.smarttravel.screens.groups

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.magic.smarttravel.R
import com.magic.smarttravel.entity.Group
import com.magic.smarttravel.screens.new_group.CreateGroupActivity
import com.magic.smarttravel.screens.tracking.TrackingActivity
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_groups.view.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Marta Turchyniak on 5/22/20.
 */
class GroupsActivity : AppCompatActivity() {

    private lateinit var adapter: GroupsAdapter
    private val groupsViewModel: GroupsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        createGroup.setOnClickListener {
            CreateGroupActivity.startActivity(this)
        }
        toolbar.arrow.setOnClickListener {
            onBackPressed()
        }
        initAdapter()
        bindViewModel()
        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        }
    }

    fun bindViewModel() {
        groupsViewModel.groups()
        groupsViewModel.groups.observe(this, Observer {
            adapter.list = it
        })
    }

    private fun initAdapter() {
        rvGroups.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = GroupsAdapter(object : GroupsAdapter.InteractionListener {
            override fun onClick(group: Group) {
                TrackingActivity.startActivity( this@GroupsActivity, group)
            }
        })
        rvGroups.adapter = adapter
    }

    companion object {
        const val REQUEST_LOCATION = 12
        fun startActivity(context: Context) {
            val intent = Intent(context, GroupsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

}

