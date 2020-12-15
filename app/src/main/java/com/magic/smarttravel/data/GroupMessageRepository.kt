package com.magic.smarttravel.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.magic.smarttravel.entity.GroupMessage

class GroupMessageRepository : FirebaseRepository<GroupMessage>(GroupMessage::class.java) {

    override fun table() = "chat_messages"

    fun insertMessage(
        groupId: String,
        message: GroupMessage,
        completion: ((exception: Exception?) -> Unit)? = null
    ) {
        databaseRef.child(groupId).push().setValue(message)
            .addOnCompleteListener {
                it.exception?.printStackTrace()
                completion?.invoke(it.exception)
            }
    }

    fun getLiveByGroupId(
        groupId: String,
        onSuccess: (entities: List<GroupMessage>) -> Unit,
        onError: (() -> Unit)? = null
    ): CancelSignal {
        val databaseReference = databaseRef.child(groupId).orderByChild("timestamp")

        val listener = object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                onError?.invoke()
            }

            override fun onDataChange(data: DataSnapshot) {
                mapEntities(data, onSuccess)
            }
        }
        databaseReference.addValueEventListener(listener)

        return QueryCancelSignal(listener, databaseReference)
    }
}