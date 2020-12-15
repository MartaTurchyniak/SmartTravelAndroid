package com.magic.smarttravel.screens.groups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.new_group.CreateGroupActivity
import kotlinx.android.synthetic.main.no_groups_activity.*

/**
 * Created by Marta Turchyniak on 5/21/20.
 */
class NoGroupsAvailableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_groups_activity)
        createGroup.setOnClickListener {
            CreateGroupActivity.startActivity(this)
        }
    }

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, NoGroupsAvailableActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}