package me.venko.presencetracker.ui.tracker

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_tracker.*
import me.venko.presencetracker.R
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
        btSettings.setOnClickListener { displaySettings() }
        requestLocationPermissions()
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
