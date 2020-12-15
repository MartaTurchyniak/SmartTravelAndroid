package com.magic.smarttravel.screens.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.magic.smarttravel.common.CommonViewModel
import com.magic.smarttravel.data.*
import com.magic.smarttravel.entity.Group
import com.magic.smarttravel.entity.GroupMessage
import com.magic.smarttravel.entity.TravelUser
import java.util.*

class GroupChatViewModel(
    private val manager: AuthorizationManager,
    private val sharedPrefs: SharedPrefs,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val groupMessageRepository: GroupMessageRepository
) : CommonViewModel() {

    private val group = MutableLiveData<Group>()
    private val messages = MutableLiveData<List<ChatMessage>>()
    private val currentUser = MutableLiveData<TravelUser>()

    init {
        fetchCurrentUser()
        fetchGroup()
        fetchMessages()
    }

    fun messages(): LiveData<List<ChatMessage>> = this.messages
    fun group(): LiveData<Group> = this.group

    fun send(message: String) {
        currentUser.value?.let { user ->
            val groupId = sharedPrefs.getGroupId()

            groupMessageRepository.insertMessage(
                groupId = groupId,
                message = GroupMessage(
                    message = message,
                    timestamp = Date(),
                    sender = GroupMessage.Sender(
                        userId = user.id,
                        firstName = user.firstName ?: "Unknown",
                        lastName = user.lastName ?: "User",
                        avatar = user.avatar
                    )
                )
            )
        }
    }

    private fun fetchMessages() {
        val groupId = sharedPrefs.getGroupId()
        signals.add(groupMessageRepository.getLiveByGroupId(groupId, { entities ->
            mapToUIMessages(entities)
        }))
    }

    private fun mapToUIMessages(entities: List<GroupMessage>) {
        manager.firebaseUser()?.uid?.let { currentUserId ->
            messages.value = entities.map { groupMessage ->
                val type = if (currentUserId == groupMessage.sender.userId)
                    ChatMessage.MessageType.OUTGOING
                else
                    ChatMessage.MessageType.INCOMING
                ChatMessage(type = type, text = groupMessage.message, sender = groupMessage.sender)
            }
        }
    }

    private fun fetchGroup() {
        val groupId = sharedPrefs.getGroupId()
        signals.add(groupRepository.getLive(entityId = groupId, onSuccess = { group ->
            group?.let { this.group.postValue(it) }
        }))
    }

    private fun fetchCurrentUser() {
        manager.firebaseUser()?.uid?.let {
            signals.add(userRepository.getLive(it, { user ->
                user?.let { this.currentUser.postValue(it) }
            }))
        }
    }
}
