package com.magic.smarttravel.screens.groups.group_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magic.smarttravel.common.CommonViewModel
import com.magic.smarttravel.data.GroupRepository
import com.magic.smarttravel.data.SharedPrefs
import com.magic.smarttravel.data.UserRepository
import com.magic.smarttravel.entity.Group
import com.magic.smarttravel.entity.TravelUser

/**
 * Created by Marta Turchyniak on 12/10/20.
 */
class GroupDetailsViewModel(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs
) : CommonViewModel() {

    val users = MutableLiveData<List<TravelUser>>()
    val groupDetails = MutableLiveData<Group>()

    fun getCurrentGroupDetails() {
        val groupId = sharedPrefs.getGroupId()
        signals.add(groupRepository.getSingle(entityId = groupId, onSuccess = { group ->
            groupDetails.postValue(group)
            group?.let { groupUsers ->
                signals.add(userRepository.getAll({ allUsers ->
                    val usersInGroup =
                        allUsers.filter { users -> groupUsers.groupUsers.firstOrNull { it.id == users.id } != null }
                    users.postValue(usersInGroup)
                }))
            }
        }))
    }
}