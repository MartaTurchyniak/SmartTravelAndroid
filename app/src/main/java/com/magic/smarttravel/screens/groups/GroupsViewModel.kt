package com.magic.smarttravel.screens.groups

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magic.smarttravel.data.AuthorizationManager
import com.magic.smarttravel.data.FirebaseRepository
import com.magic.smarttravel.data.GroupRepository
import com.magic.smarttravel.data.UserRepository
import com.magic.smarttravel.entity.Group

/**
 * Created by Marta Turchyniak on 12/9/20.
 */
class GroupsViewModel(
    private val userRepository: UserRepository,
    private val manager: AuthorizationManager,
    private val groupsRepository: GroupRepository
) : ViewModel() {

    private var cancelSignal: FirebaseRepository.CancelSignal? = null
    var groups: MutableLiveData<List<Group>> = MutableLiveData()

    fun groups() {
        this.cancelSignal = groupsRepository.getAll({ groups ->
            this.cancelSignal = manager.firebaseUser()?.let { firebaseUser ->
                userRepository.getSingle(firebaseUser.uid, { user ->
                    val filteredGroups =
                        groups.filter { group -> user?.groupIds?.firstOrNull { it == group.id } != null }
                    this.groups.postValue(filteredGroups)
                })
            }
        })
    }
}