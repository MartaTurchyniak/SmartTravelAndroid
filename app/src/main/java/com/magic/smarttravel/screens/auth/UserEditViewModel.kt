package com.magic.smarttravel.screens.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.magic.smarttravel.common.CommonViewModel
import com.magic.smarttravel.data.AuthorizationManager
import com.magic.smarttravel.data.GroupRepository
import com.magic.smarttravel.data.InviteRepository
import com.magic.smarttravel.data.UserRepository
import com.magic.smarttravel.entity.Group
import com.magic.smarttravel.entity.GroupInvite
import com.magic.smarttravel.entity.TravelUser
import com.magic.smarttravel.util.livedata.SingleLiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Marta Turchyniak on 12/6/20.
 */
class UserEditViewModel(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val inviteRepository: InviteRepository,
    private val manager: AuthorizationManager
) : CommonViewModel() {

    private val signInCompletedAction = SingleLiveEvent<SignInNextStep>()

    fun signInCompletedAction(): LiveData<SignInNextStep> = signInCompletedAction

    val travelUser = MutableLiveData<TravelUser>()

    init {
        fetchUser()
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

    fun updateUser(firstName: String, lastName: String) {
        travelUser.value?.let { user ->
            user.firstName = firstName
            user.lastName = lastName

            inviteRepository.getAllOnce({ invites ->
                val userInvites = invites.filter { it.userPhone == user.phone }
                user.groupIds.addAll(userInvites.map { it.groupId })

                userRepository.put(
                    entityId = user.id,
                    entity = user,
                    completion = { exception ->
                        if (exception == null) {
                            addUserToGroups(user, userInvites)
                        }
                    }
                )
            })
        }
    }

    private fun addUserToGroups(user: TravelUser, invites: List<GroupInvite>) {
        val groupIds = invites.map { it.groupId }
        groupRepository.getAll({ groups ->
            groups.forEach { group ->
                if (groupIds.contains(group.id)) {
                    group.groupUsers.add(Group.Member(user.id, user.firstName!!))
                    groupRepository.put(group.id, group)
                }
            }
        })
        deleteAcceptedInvites(
            invites = invites,
            nextStep = if (user.groupIds.isEmpty()) SignInNextStep.NO_GROUPS else SignInNextStep.GROUPS
        )
    }

    private fun deleteAcceptedInvites(invites: List<GroupInvite>, nextStep: SignInNextStep) {
        invites.forEach { inviteRepository.delete(it.id) }

        GlobalScope.launch {
            delay(500)
            signInCompletedAction.postValue(nextStep)
        }
    }
}