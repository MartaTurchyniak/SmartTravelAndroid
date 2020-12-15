package com.magic.smarttravel.entity

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

/**
 * Created by Marta Turchyniak on 12/8/20.
 */
data class Group(
    var id: String = "",
    var name: String = "",
    var groupUsers: MutableList<Member> = mutableListOf(),
    var startLocationLat: Double? = null,
    var startLocationLon: Double? = null,
    var endLocationLat: Double? = null,
    var endLocationLon: Double? = null
) : Serializable {

    fun route(): Pair<LatLng, LatLng>? {
        val startLat = this.startLocationLat ?: return null
        val startLong = this.startLocationLon ?: return null
        val endLat = this.endLocationLat ?: return null
        val endLong = this.endLocationLon ?: return null

        return Pair(LatLng(startLat, startLong), LatLng(endLat, endLong))
    }

    data class Member(
        var id: String = "",
        var firstName: String = "",
        var lat: Double? = null,
        var long: Double? = null
    ) : Serializable {

        fun location(): LatLng? {
            val lat = this.lat ?: return null
            val long = this.long ?: return null

            return LatLng(lat, long)
        }
    }
}