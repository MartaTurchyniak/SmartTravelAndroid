package com.magic.smarttravel.screens.auth

import androidx.lifecycle.LiveData
import com.magic.smarttravel.common.CommonViewModel
import com.magic.smarttravel.data.AuthorizationManager
import com.magic.smarttravel.data.UserRepository
import com.magic.smarttravel.entity.TravelUser
import com.magic.smarttravel.util.livedata.SingleLiveEvent

class SignInViewModel(
    private val manager: AuthorizationManager,
    private val repository: UserRepository
) : CommonViewModel() {

    private val signInCompletedAction = SingleLiveEvent<SignInNextStep>()
    private val startAuthorizationFlowAction = SingleLiveEvent<Unit>()

    init {
        if (this.manager.isAuthorized()) {
            onAuthorized()
        } else {
            startAuthorizationFlowAction.call()
        }
    }

    fun signInCompletedAction(): LiveData<SignInNextStep> = signInCompletedAction
    fun startAuthorizationFlowAction(): LiveData<Unit> = startAuthorizationFlowAction

    fun onAuthorized() {
        manager.firebaseUser()?.let { firebaseUser ->
            repository.getSingle(firebaseUser.uid, { user ->
                if (user == null) {
                    createUser(firebaseUser.uid, firebaseUser.phoneNumber!!)
                } else {
                    if (user.registrationCompleted()) {
                        if (user.groupIds.size == 0) {
                            signInCompletedAction.postValue(SignInNextStep.NO_GROUPS)
                        } else {
                            signInCompletedAction.postValue(SignInNextStep.GROUPS)
                        }
                    } else {
                        signInCompletedAction.postValue(SignInNextStep.USER_EDIT)
                    }
                }
            })
        }
    }

    private fun createUser(userId: String, phoneNumber: String) {
        val user = TravelUser(userId, phoneNumber)

        repository.put(userId, user) {
            signInCompletedAction.postValue(SignInNextStep.USER_EDIT)
        }
    }
}

enum class SignInNextStep {
    USER_EDIT, NO_GROUPS, GROUPS
}