package com.magic.smarttravel.screens.tracking

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.magic.smarttravel.common.CommonViewModel
import com.magic.smarttravel.data.AuthorizationManager
import com.magic.smarttravel.data.GroupRepository
import com.magic.smarttravel.data.SharedPrefs
import com.magic.smarttravel.entity.Group

/**
 * Created by Marta Turchyniak on 12/9/20.
 */
class MapViewModel(
    private val sharedPref: SharedPrefs,
    private val groupRepository: GroupRepository,
    private val manager: AuthorizationManager
) : CommonViewModel() {

    private val groupDetails = MutableLiveData<Group>()

    init {
        this.observeGroup()
    }

    fun groupDetails(): MutableLiveData<Group> = this.groupDetails

    fun updateGroupDestination(startDestination: LatLng, endDestination: LatLng) {
        groupRepository.getSingle(sharedPref.getGroupId(), { group ->
            group?.let {
                group.startLocationLat = startDestination.latitude
                group.startLocationLon = startDestination.longitude
                group.endLocationLat = endDestination.latitude
                group.endLocationLon = endDestination.longitude
                groupRepository.put(sharedPref.getGroupId(), group)
            }
        })
    }

    fun updateLocation(location: Location) {
        manager.firebaseUser()?.uid?.let { currentUserId ->
            groupDetails.value?.let { group ->
                group.groupUsers.firstOrNull { it.id == currentUserId }?.let { member ->
                    member.lat = location.latitude
                    member.long = location.longitude
                }
                groupRepository.put(group.id, group)
            }
        }
    }

    private fun observeGroup() {
        signals.add(groupRepository.getLive(sharedPref.getGroupId(), { group ->
            group?.let { groupDetails.postValue(it) }
        }))
    }
}