package me.venko.presencetracker.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_settings.*
import me.venko.presencetracker.R
import me.venko.presencetracker.data.Map
import me.venko.presencetracker.ui.tracker.BaseFragment
import me.venko.presencetracker.utils.loge
import me.venko.presencetracker.utils.newRadialBounds
import me.venko.presencetracker.utils.popBackStack
import me.venko.presencetracker.viewmodel.SettingsViewModel
import pub.devrel.easypermissions.EasyPermissions


/**
 * @author Victor Kosenko
 *
 */
class SettingsFragment : BaseFragment() {

    private lateinit var viewModel: SettingsViewModel

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

        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        viewModel.ssidName.observe(this, Observer {
            etSsidName.setText(it)
        })

        btSave.setOnClickListener {
            viewModel.saveSsid(etSsidName.text.toString())
            popBackStack()
        }
        mapLocation.getMapAsync { googleMap ->
            if (EasyPermissions.hasPermissions(
                            context!!,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )) {
                googleMap.isMyLocationEnabled = true
            }
            viewModel.locationBounds.observe(this, Observer {

                val cameraPosition = LatLng(it.center.latitude, it.center.longitude)
                val cameraUpdate = if (it.radius > 0.0) {
                    CameraUpdateFactory.newLatLngBounds(newRadialBounds(cameraPosition, it.radius), 0)
                } else {
                    CameraUpdateFactory.newLatLngZoom(cameraPosition, Map.DEFAULT_ZOOM_FACTOR)
                }
                googleMap.moveCamera(cameraUpdate)
            })
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