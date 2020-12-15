package com.magic.smarttravel.screens.new_group

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
import com.magic.smarttravel.util.formatters.PhoneNumberFormatter
import com.magic.smarttravel.util.livedata.SingleLiveEvent

/**
 * Created by Marta Turchyniak on 12/8/20.
 */
class NewGroupViewModel(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val inviteRepository: InviteRepository,
    manager: AuthorizationManager
) : CommonViewModel() {

    private val user = MutableLiveData<TravelUser>()

    private val groupCreatedAction = SingleLiveEvent<Unit>()

    init {
        signals.add(userRepository.getSingle(manager.firebaseUser()!!.uid, { currentUser ->
            currentUser?.let { user.postValue(it) }
        }))
    }

    fun groupCreated(): LiveData<Unit> = this.groupCreatedAction

    fun createNewGroup(name: String, contacts: ContactList) {
        user.value?.let { currentUser ->
            signals.add(userRepository.getAll({ allUsers ->
                val phoneNumbers = extractNumbers(contacts)

                val notRegisteredPhoneNumbers = phoneNumbers.filter { number ->
                    allUsers.firstOrNull { it.phone == number } == null
                }

                val registeredNumbers = allUsers.filter { user ->
                    phoneNumbers.firstOrNull { it == user.phone } != null
                }

                val newGroupRef = groupRepository.newRef()
                val groupUsers = mutableListOf(
                    Group.Member(
                        id = currentUser.id, firstName = currentUser.firstName!!
                    )
                )
                updateGroup(registeredNumbers, groupUsers, newGroupRef, name)

                registeredNumbers.forEach { user ->
                    user.groupIds.add(newGroupRef)
                    userRepository.put(user.id, user)
                }

                notRegisteredPhoneNumbers.forEach { phone ->
                    inviteRepository.put(
                        inviteRepository.newRef(), GroupInvite(
                            userPhone = phone, groupId = newGroupRef
                        )
                    )
                }

                this.updateCurrentUser(newGroupRef)
            }))
        }
    }

    private fun updateGroup(
        registeredNumbers: List<TravelUser>,
        groupUsers: MutableList<Group.Member>,
        newGroupRef: String,
        name: String
    ) {
        registeredNumbers.forEach {
            groupUsers.add(Group.Member(it.id, it.firstName!!))
        }
        val group = Group(
            id = newGroupRef,
            name = name,
            groupUsers = groupUsers
        )

        groupRepository.put(newGroupRef, group)
    }

    private fun updateCurrentUser(newGroupRef: String) {
        this.user.value?.let { user ->
            user.groupIds.add(newGroupRef)
            userRepository.put(user.id, user) {
                dispatchSuccess()
            }
        }
    }

    private fun dispatchSuccess() {
        groupCreatedAction.call()
    }

    private fun extractNumbers(contacts: ContactList) =
        contacts.list.mapNotNull { it.mobileNumber }
            .map { PhoneNumberFormatter.format(it) }
}