package me.venko.presencetracker.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_settings.*
import me.venko.presencetracker.R
import me.venko.presencetracker.data.Map
import me.venko.presencetracker.data.tracker.LocationBounds
import me.venko.presencetracker.ui.tracker.BaseFragment
import me.venko.presencetracker.utils.*
import me.venko.presencetracker.viewmodel.LocationTrackerViewModel
import me.venko.presencetracker.viewmodel.SettingsViewModel
import pub.devrel.easypermissions.EasyPermissions


/**
 * @author Victor Kosenko
 *
 */
class SettingsFragment : BaseFragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var locationViewModel: LocationTrackerViewModel
    private var locationBounds: LocationBounds? = null
    private var updatedLocationBounds = LocationBounds(Location(""), 0f)
    private var lastKnownLocation: Location? = null
    private var map: GoogleMap? = null
    private var isCameraSet = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            var mapState: Bundle? = null
            if (savedInstanceState != null) {
                mapState = savedInstanceState.getBundle(KEY_MAP_STATE)
            }
            mapLocation?.onCreate(mapState)
        } catch (e: Exception) {
            loge(e) { "Error calling onCreate on map view" }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapState = Bundle()
        mapLocation?.onSaveInstanceState(mapState)
        outState.putBundle(KEY_MAP_STATE, mapState)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        settingsViewModel.ssidName.observe(this, Observer {
            etSsidName.setText(it)
        })
        locationViewModel = ViewModelProviders.of(activity!!).get(LocationTrackerViewModel::class.java)
        if (hasLocationPermissions()) {
            locationViewModel.onLocationPermissionsGranted()
            locationViewModel.lastKnownLocation.observe(this, Observer { location ->
                lastKnownLocation = location
                locationBounds?.let {
                    if (!isCameraSet && it.isEmpty()) {
                        updateCamera(LocationBounds(location, 0f), map)
                    }
                }
            })
        }

        btSave.setOnClickListener {
            settingsViewModel.saveSsid(etSsidName.text.toString())
            settingsViewModel.saveLocationBounds(updatedLocationBounds)
            popBackStack()
        }
        val mapPadding = resources.getDimensionPixelSize(R.dimen.view_padding_regular)
        mapLocation.getMapAsync { googleMap ->
            map = googleMap
            // Map padding should be the same as radius overlay margin for radius evaluation
            // precision
            googleMap.setPadding(mapPadding, mapPadding, mapPadding, mapPadding)
            if (hasLocationPermissions()) {
                googleMap.isMyLocationEnabled = true
            }
            settingsViewModel.locationBounds.observe(this, Observer { bounds ->
                locationBounds = bounds
                if (!isCameraSet) {
                    if (bounds.isEmpty()) {
                        lastKnownLocation?.let {
                            updateCamera(LocationBounds(it, 0f), googleMap)
                        } ?: run {
                            logw { "Skipping camera update until location received" }
                        }
                    } else {
                        updateCamera(bounds, googleMap)
                    }
                }
            })

            googleMap.setOnCameraIdleListener { onCameraIdle(googleMap) }
            googleMap.setOnCameraMoveStartedListener {
                // handle movement initiated by user
                isCameraSet = true
            }
        }
    }

    private fun updateCamera(bounds: LocationBounds, googleMap: GoogleMap?) {
        isCameraSet = true
        val cameraPosition = LatLng(bounds.center.latitude, bounds.center.longitude)
        val cameraUpdate = if (bounds.radius > 0f) {
            CameraUpdateFactory.newLatLngBounds(newRadialBounds(cameraPosition, bounds.radius), 0)
        } else {
            CameraUpdateFactory.newLatLngZoom(cameraPosition, Map.DEFAULT_ZOOM_FACTOR)
        }
        googleMap?.moveCamera(cameraUpdate)
    }

    private fun hasLocationPermissions(): Boolean = EasyPermissions.hasPermissions(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private fun onCameraIdle(googleMap: GoogleMap) {
        val vr = googleMap.projection.visibleRegion
        val right = vr.latLngBounds.northeast.longitude
        val bottom = vr.latLngBounds.southwest.latitude
        val anchorPoint = if (mapLocation.width <= mapLocation.height) {
            LatLng(vr.latLngBounds.center.latitude, right)
        } else {
            LatLng(bottom, vr.latLngBounds.center.longitude)
        }
        val center = Location("").apply {
            latitude = vr.latLngBounds.center.latitude
            longitude = vr.latLngBounds.center.longitude
        }
        val distance = evalDistanceInM(vr.latLngBounds.center, anchorPoint)

        updatedLocationBounds.apply {
            this.center.let {
                it.latitude = center.latitude
                it.longitude = center.longitude
            }
            this.radius = distance.toFloat()
        }
    }

    override fun onResume() {
        super.onResume()
        mapLocation?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapLocation?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapLocation?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapLocation?.onDestroy()
    }


    companion object {
        private const val KEY_MAP_STATE = "mapState"

        fun newInstance() = SettingsFragment()
    }
}