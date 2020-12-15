package com.magic.smarttravel.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.magic.smarttravel.entity.Group

/**
 * Created by Marta Turchyniak on 12/8/20.
 */
class GroupRepository : FirebaseRepository<Group>(Group::class.java) {

    override fun table() = "groups"

    fun getAll(
        onSuccess: (entity: List<Group>) -> Unit,
        onError: (() -> Unit)? = null
    ): CancelSignal {
        val listener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mapGroups(dataSnapshot, onSuccess)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(table(), "error: ${databaseError.message}")
                onError?.invoke()
            }
        }
        databaseRef.addListenerForSingleValueEvent(listener)
        return ReferenceCancelSignal(listener, databaseRef)
    }

    private fun mapGroups(data: DataSnapshot, onSuccess: (entity: List<Group>) -> Unit) {
        if (data.exists()) {
            onSuccess.invoke(data.children.mapNotNull { it.getValue(Group::class.java) })
        } else {
            onSuccess.invoke(emptyList())
        }
    }
}