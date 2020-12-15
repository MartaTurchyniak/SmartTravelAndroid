package com.magic.smarttravel.screens.tracking

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.directions.route.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.magic.smarttravel.R
import com.magic.smarttravel.entity.Group
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback, RoutingListener {

    private var map: GoogleMap? = null
    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null
    private val viewModel: MapViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        activity?.let { fragmentActivity ->
            if (ActivityCompat.checkSelfPermission(
                    fragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    fragmentActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    fragmentActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION
                )
            } else {
                retrieveLocation()
            }
        }
    }

    private fun retrieveLocation() {
        SmartLocation
            .with(context)
            .location()
            .config(LocationParams.NAVIGATION)
            .continuous()
            .start {
                viewModel.updateLocation(it)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.groupDetails().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            bindGroupDetails(it)
        })
    }

    private val userMarkers = mutableListOf<Marker>()

    private fun bindGroupDetails(group: Group) {
        val map = this.map ?: return

        userMarkers.forEach { it.remove() }
        userMarkers.clear()
        group.groupUsers.forEachIndexed { index, groupMember ->
            groupMember.location()?.let { memberLocation ->
                val marker = map.addMarker(
                    MarkerOptions().position(memberLocation)
                        .title(groupMember.firstName)
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                when (index) {
                                    0 -> BitmapDescriptorFactory.HUE_ROSE
                                    1 -> BitmapDescriptorFactory.HUE_CYAN
                                    else -> BitmapDescriptorFactory.HUE_VIOLET
                                }
                            )
                        )
                )
                marker.showInfoWindow()
                userMarkers.add(marker)
            }
        }

        group.route()?.let {
            val (start, end) = it
            drawRoute(start, end)
        }

        userMarkers.firstOrNull()?.let {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, DEFAULT_MAP_ZOOM))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        this.viewModel.groupDetails().value?.let {
            bindGroupDetails(it)
        }
    }

    private fun drawRoute(start: LatLng, end: LatLng) {
        val routing = Routing.Builder()
            .travelMode(AbstractRouting.TravelMode.DRIVING)
            .withListener(this)
            .waypoints(start, end)
            .key("AIzaSyBVP6oezLvnDWB_ycMGHPDe-hzUf_FntKg")
            .build()
        routing.execute()
    }

    private fun getPlace(destination: Autocomplete) {
        val placesClient = activity?.let { Places.createClient(it) }
        val placeId =
            if (startLatLng == null) destination.startDestination?.placeId else destination.endDestination?.placeId
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        placeId?.let {
            val request = FetchPlaceRequest.newInstance(it, placeFields)
            placesClient?.fetchPlace(request)
                ?.addOnSuccessListener { response: FetchPlaceResponse ->
                    if (startLatLng == null) {
                        startLatLng = response.place.latLng
                        getPlace(destination)
                    } else {
                        endLatLng = response.place.latLng
                        viewModel.updateGroupDestination(startLatLng!!, endLatLng!!)
                    }
                    Log.i("place", "Place found: ${response.place.name}")
                }?.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Log.e("place", "Place not found: ${exception.message}")
                        val statusCode = exception.statusCode
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as TrackingActivity).destination?.let { getPlace(it) }
    }

    override fun onRoutingCancelled() {
        Log.e("check", "onRoutingCancelled");
    }

    override fun onRoutingStart() {
        Log.e("check", "onRoutingStart");
    }

    override fun onRoutingFailure(e: RouteException) {
        Log.e("check", e.message)
    }

    private val routeMarkers = mutableListOf<Marker>()
    private val routePolylines = mutableListOf<Polyline>()

    override fun onRoutingSuccess(route: ArrayList<Route>, p1: Int) {
        Log.e("check", "onRoutingSuccess")

        val map = this.map ?: return

        routeMarkers.forEach { it.remove() }
        routeMarkers.clear()

        this.viewModel.groupDetails().value?.route()?.let {
            val (start, end) = it

            routeMarkers.add(
                createMarker(map, start, "start", BitmapDescriptorFactory.HUE_GREEN)
            )
            routeMarkers.add(
                createMarker(map, end, "finish", BitmapDescriptorFactory.HUE_RED)
            )
        }

        routePolylines.forEach { it.remove() }
        routePolylines.clear()

        for (i in 0 until route.size) {
            val polyOptions = PolylineOptions()
            route.lastIndex
            polyOptions.color(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            polyOptions.width(10 + i * 4.toFloat())
            polyOptions.addAll(route[i].points)

            val polyline = map.addPolyline(polyOptions)
            routePolylines.add(polyline)
        }
    }

    private fun createMarker(
        map: GoogleMap,
        position: LatLng,
        title: String,
        color: Float
    ): Marker {
        return map.addMarker(
            MarkerOptions().position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(color))
        ).also {
            it.showInfoWindow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SmartLocation.with(context).location().stop()
    }

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val DEFAULT_MAP_ZOOM = 12.0f
    }
}
