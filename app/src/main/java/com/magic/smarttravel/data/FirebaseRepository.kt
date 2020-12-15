package com.magic.smarttravel.data

import android.util.Log
import com.google.firebase.database.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class FirebaseRepository<Entity : Any>(private val entityClass: Class<Entity>) {

    protected abstract fun table(): String

    protected val databaseRef by lazy {
        FirebaseDatabase.getInstance().getReference(table())
    }

    fun put(
        entityId: String,
        entity: Entity,
        completion: ((exception: Exception?) -> Unit)? = null
    ) {
        databaseRef.child(entityId).setValue(entity).addOnCompleteListener {
            it.exception?.printStackTrace()
            completion?.invoke(it.exception)
        }
    }

    fun delete(entityId: String, completion: ((exception: Exception?) -> Unit)? = null) {
        databaseRef.child(entityId).setValue(null).addOnCompleteListener {
            it.exception?.printStackTrace()
            completion?.invoke(it.exception)
        }
    }


    fun newRef(): String {
        return databaseRef.push().key!!
    }

    fun getSingle(
        entityId: String,
        onSuccess: (entity: Entity?) -> Unit,
        onError: (() -> Unit)? = null
    ): CancelSignal {
        val databaseReference = databaseRef.child(entityId)

        val listener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d(table(), "error: ${error.message}")
                onError?.invoke()
            }

            override fun onDataChange(data: DataSnapshot) {
                mapEntity(data, onSuccess)
            }
        }
        databaseReference.addListenerForSingleValueEvent(listener)

        return ReferenceCancelSignal(listener, databaseReference)
    }

    fun getAllOnce(
        onSuccess: (entity: List<Entity>) -> Unit,
        onError: (() -> Unit)? = null
    ): CancelSignal {
        val databaseReference = databaseRef

        val listener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d(table(), "error: ${error.message}")
                onError?.invoke()
            }

            override fun onDataChange(data: DataSnapshot) {
                mapEntities(data, onSuccess)
            }
        }
        databaseReference.addListenerForSingleValueEvent(listener)

        return ReferenceCancelSignal(listener, databaseReference)
    }

    fun getLive(
        entityId: String,
        onSuccess: (entity: Entity?) -> Unit,
        onError: (() -> Unit)? = null
    ): CancelSignal {
        val databaseReference = databaseRef.child(entityId)

        val listener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                onError?.invoke()
            }

            override fun onDataChange(data: DataSnapshot) {
                mapEntity(data, onSuccess)
            }
        }
        databaseReference.addValueEventListener(listener)

        return ReferenceCancelSignal(listener, databaseReference)
    }

    protected fun mapEntity(
        data: DataSnapshot,
        onSuccess: (entity: Entity?) -> Unit
    ) {
        if (data.exists()) {
            onSuccess.invoke(data.getValue(entityClass))
        } else {
            onSuccess.invoke(null)
        }
    }

    protected fun mapEntities(
        data: DataSnapshot,
        onSuccess: (entities: List<Entity>) -> Unit
    ) {
        if (data.exists()) {
            onSuccess.invoke(data.children.mapNotNull { it.getValue(entityClass) })
        } else {
            onSuccess.invoke(emptyList())
        }
    }

    interface CancelSignal {

        fun cancel()
    }

    class ReferenceCancelSignal(
        private val valueEventListener: ValueEventListener,
        private val ref: DatabaseReference
    ) : CancelSignal {
        override fun cancel() {
            ref.removeEventListener(valueEventListener)
        }
    }

    class QueryCancelSignal(
        private val valueEventListener: ValueEventListener,
        private val query: Query
    ) : CancelSignal {
        override fun cancel() {
            query.removeEventListener(valueEventListener)
        }
    }
}