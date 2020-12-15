package com.magic.smarttravel.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.groups.GroupsActivity
import com.magic.smarttravel.screens.groups.NoGroupsAvailableActivity
import kotlinx.android.synthetic.main.user_edit_activity.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Marta Turchyniak on 5/21/20.
 */
class UserEditActivity : AppCompatActivity() {

    private val userEditViewModel: UserEditViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_edit_activity)
        attachListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        userEditViewModel.signInCompletedAction().observe(this, Observer { nextStep ->
            if (nextStep == SignInNextStep.NO_GROUPS) {
                NoGroupsAvailableActivity.startActivity(this)
            } else {
                GroupsActivity.startActivity(this)
            }
            finish()
        })
        userEditViewModel.travelUser.observe(this, Observer {
            phone.setText(it.phone)
        })
    }

    private fun attachListeners() {
        register.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            if (firstName.isNotBlank() && lastName.isNotBlank()) {
                userEditViewModel.updateUser(firstName.trim(), lastName.trim())
            }
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, UserEditActivity::class.java)
            context.startActivity(intent)
        }
    }

}