package com.magic.smarttravel.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Created by Marta Turchyniak on 5/23/20.
 */
class AuthorizationManager {

    fun isAuthorized(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun firebaseUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    fun signOut() {
        Firebase.auth.signOut()
    }
}