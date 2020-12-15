package com.magic.smarttravel.screens.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.magic.smarttravel.R
import kotlinx.android.synthetic.main.fragment_chat.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by Marta Turchyniak on 12/1/20.
 */
class ChatFragment : Fragment() {

    private val viewModel: GroupChatViewModel by sharedViewModel()

    private lateinit var chatAdapter: ChatAdapter

    private val scrollRunnable = Runnable {
        rvChat?.post {
            rvChat?.let {
                it.smoothScrollBy(
                    0,
                    it.computeVerticalScrollRange(),
                    AccelerateDecelerateInterpolator(),
                    AUTO_SCROLL_DURATION
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        attachListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.messages().observe(viewLifecycleOwner, Observer {
            chatAdapter.update(it)
            scrollToLast()
        })
    }

    private fun attachListeners() {
        btnSend.setOnClickListener { sendMessage() }
    }

    private fun setupUI() {
        rvChat.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        chatAdapter = ChatAdapter()
        rvChat.adapter = chatAdapter
        rvChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (kotlin.math.abs(oldBottom - bottom) != 0) {
                scrollToLast()
            }
        }
    }

    private fun scrollToLast() {
        rvChat.removeCallbacks(scrollRunnable)
        rvChat.postDelayed(scrollRunnable, AUTO_SCROLL_DELAY)
    }

    private fun sendMessage() {
        val message = etChatInput.text.toString()
        if (message.isNotBlank()) {
            etChatInput.text.clear()
            viewModel.send(message)
        }
    }

    companion object {
        private const val AUTO_SCROLL_DURATION = 800
        private const val AUTO_SCROLL_DELAY = 100L
    }
}