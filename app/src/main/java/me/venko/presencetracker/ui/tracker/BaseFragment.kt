package me.venko.presencetracker.ui.tracker

import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author Victor Kosenko
 *
 */
open class BaseFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

}