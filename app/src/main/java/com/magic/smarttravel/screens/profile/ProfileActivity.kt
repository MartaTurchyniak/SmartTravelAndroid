package com.magic.smarttravel.screens.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.auth.SignInActivity
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Marta Turchyniak on 5/24/20.
 */
class ProfileActivity : AppCompatActivity() {

    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        signOut.setOnClickListener {
            profileViewModel.logout()
            SignInActivity.start(this)
        }
        arrow.setOnClickListener { onBackPressed() }
        bindViewModel()
    }

    private fun bindViewModel() {
        profileViewModel.travelUser.observe(this, Observer {
            etFirstName.setText(it.firstName)
            etLastName.setText(it.lastName)
            etPhone.setText(it.phone)
        })

    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }
}