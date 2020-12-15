package com.magic.smarttravel.screens.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.magic.smarttravel.R
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.android.viewmodel.ext.android.viewModel

class ChatActivity : AppCompatActivity() {

    private val viewModel: GroupChatViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_chat)
        this.attachListeners()
        this.observeViewModel()
    }

    private fun attachListeners() {
        arrow.setOnClickListener { onBackPressed() }
    }

    private fun observeViewModel() {
        viewModel.group().observe(this, Observer {
            tvTitle.text = it.name
        })
    }
}