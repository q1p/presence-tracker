package me.venko.presencetracker.ui.tracker

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import me.venko.presencetracker.R
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
        requestLocationPermissions()
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

}
