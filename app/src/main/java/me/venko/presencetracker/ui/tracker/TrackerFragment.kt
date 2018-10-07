package me.venko.presencetracker.ui.tracker

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_tracker.*
import me.venko.presencetracker.R
import me.venko.presencetracker.data.tracker.PresenceState
import me.venko.presencetracker.data.tracker.PresenceState.*
import me.venko.presencetracker.ui.settings.SettingsFragment
import me.venko.presencetracker.utils.replaceFragmentAnimate
import me.venko.presencetracker.viewmodel.TrackerViewModel
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


/**
 * @author Victor Kosenko
 */
class TrackerFragment : BaseFragment() {

    private lateinit var viewModel: TrackerViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_tracker, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrackerViewModel::class.java)
        viewModel.presenceState.observe(this, Observer {
            tvStatus.text = getPresenceStatus(it)
        })
        btSettings.setOnClickListener { displaySettings() }
        requestLocationPermissions()
    }

    private fun getPresenceStatus(presenceState: PresenceState): String {
        val statusRes = when (presenceState) {
            EVALUATING -> R.string.tv_tracker_status_evaluating
            INSIDE_GEO -> R.string.tv_tracker_status_present_geo
            INSIDE_WIFI -> R.string.tv_tracker_status_present_wifi
            INSIDE_BOTH -> R.string.tv_tracker_status_present_both
            OUTSIDE -> R.string.tv_tracker_status_outside
            NO_PERMISSIONS -> R.string.tv_tracker_status_no_permissions
            UNKNOWN -> R.string.tv_tracker_status_unknown
        }
        return getString(statusRes)
    }

    private fun displaySettings() {
        replaceFragmentAnimate(SettingsFragment.newInstance(), R.id.container)
    }

    private fun requestLocationPermissions() {
        EasyPermissions.requestPermissions(PermissionRequest.Builder(
                this,
                REQUEST_LOCATION_PERMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        )

                .setRationale(R.string.dialog_rationale_location_perms)
                .setPositiveButtonText(R.string.dialog_button_ok)
                .setNegativeButtonText(R.string.dialog_button_decline)
                .build())
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (REQUEST_LOCATION_PERMS == requestCode) {
            viewModel.onLocationPermissionsGranted()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (REQUEST_LOCATION_PERMS == requestCode) {
            viewModel.onLocationPermissionsDenied()
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMS = 72

        fun newInstance() = TrackerFragment()
    }
}
