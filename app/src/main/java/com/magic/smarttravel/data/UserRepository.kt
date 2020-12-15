package com.magic.smarttravel.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.magic.smarttravel.entity.TravelUser

/**
 * Created by Marta Turchyniak on 5/23/20.
 */
class UserRepository : FirebaseRepository<TravelUser>(TravelUser::class.java) {

    override fun table() = "users"

    fun getAll(
        onSuccess: (entity: List<TravelUser>) -> Unit,
        onError: (() -> Unit)? = null
    ): CancelSignal {
        val listener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mapUsers(dataSnapshot, onSuccess)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(table(), "error: ${databaseError.message}")
                onError?.invoke()
            }
        }
        databaseRef.addListenerForSingleValueEvent(listener)
        return ReferenceCancelSignal(listener, databaseRef)
    }

    private fun mapUsers(data: DataSnapshot, onSuccess: (entity: List<TravelUser>) -> Unit) {
        if (data.exists()) {
            onSuccess.invoke(data.children.mapNotNull { it.getValue(TravelUser::class.java) })
        } else {
            onSuccess.invoke(emptyList())
        }
    }
}