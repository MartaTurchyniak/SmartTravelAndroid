package com.magic.smarttravel.screens.profile

import androidx.lifecycle.MutableLiveData
import com.magic.smarttravel.common.CommonViewModel
import com.magic.smarttravel.data.AuthorizationManager
import com.magic.smarttravel.data.UserRepository
import com.magic.smarttravel.entity.TravelUser

/**
 * Created by Marta Turchyniak on 12/8/20.
 */
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val manager: AuthorizationManager
) : CommonViewModel() {

    val travelUser = MutableLiveData<TravelUser>()

    init {
        fetchUser()
    }

    fun logout() {
        manager.signOut()
    }

    private fun fetchUser() {
        manager.firebaseUser()?.let { firebaseUser ->
            signals.add(userRepository.getSingle(firebaseUser.uid, { user ->
                user?.let {
                    travelUser.postValue(it)
                }
            }))
        }
    }
}