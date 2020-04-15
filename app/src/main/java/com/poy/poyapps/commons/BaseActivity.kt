package com.poy.poyapps.commons

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.poy.poyapps.R
import com.poy.poyapps.model.Loc
import org.jetbrains.anko.toast
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import rebus.permissionutils.PermissionEnum
import rebus.permissionutils.PermissionManager
import rebus.permissionutils.PermissionUtils

@SuppressLint("Registered")
open class BaseActivity: RxBaseActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private val connectionFailureRequest = 9000
    var mGoogleApiClient: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    var loc: Loc? = null
    var dialog: AlertDialog? = null

    fun changeFragment(f: Fragment, cleanStack: Boolean, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        if(cleanStack)
            clearBackStack(supportFragmentManager)
        hideKeyboard()
        ft.replace(R.id.main_content, f, tag)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun clearBackStack(fm: FragmentManager) {
        if (fm.backStackEntryCount > 0){
            val first = fm.getBackStackEntryAt(0)
            fm.popBackStack(first.id, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // check if no com.poy.t_rex.view has focus:
        val currentFocusedView = getCurrentFocus()
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun getPermission(){
        PermissionManager.Builder()
            .permission(
                PermissionEnum.WRITE_EXTERNAL_STORAGE,
                PermissionEnum.READ_EXTERNAL_STORAGE,
                PermissionEnum.ACCESS_FINE_LOCATION,
                PermissionEnum.ACCESS_COARSE_LOCATION,
                PermissionEnum.READ_PHONE_STATE,
                PermissionEnum.CAMERA)
            .askAgain(false)
            .ask(this)
    }

    fun getLocation() {
        val granted = PermissionUtils.isGranted(this, PermissionEnum.ACCESS_FINE_LOCATION)

        if (granted) {
            if (!isGPSEnabled() || !isNetworkEnabled()) {
                if (dialog == null)
                    showSettingsAlert()
            } else {
                dialog?.dismiss()
                if (loc == null) {
                    //createToast("lokasi tidak ditemukan", 0)
                    showSettingsAlert()
                } else {
                    val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
                        //createToast("lokasi tidak ditemukan ", 0)
                    } else {
                        loc = Loc("${location.latitude}", "${location.longitude}")
                        Utils.loc = loc
                        Utils.latitude = loc?.lat.toString()
                        Utils.longitude = loc?.long.toString()
                        Log.d("connect", ""+ Utils.loc)
                    }
                }
            }

        } else {
            getPermission()
        }
    }

    private fun getLocationManager(): LocationManager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private fun isGPSEnabled():Boolean =
        getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun isNetworkEnabled():Boolean =
        getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        val granted = PermissionUtils.isGranted(this, PermissionEnum.ACCESS_FINE_LOCATION)
        if (granted) {
            val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
                //createToast("lokasi tidak ditemukan ", 0)
            } else {
                loc = Loc("${location.latitude}", "${location.longitude}")
                Utils.loc = loc
                Utils.latitude = loc?.lat.toString()
                Utils.longitude = loc?.long.toString()
                Log.d("connect", ""+ Utils.loc)
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, connectionFailureRequest)
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (e: IntentSender.SendIntentException) {
                // Log the error
                e.printStackTrace()
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //createToast("lokasi error karena " + connectionResult.errorCode, 0)
        }
    }

    override fun onLocationChanged(location: Location) {
        loc = Loc("${location.latitude}",
            "${location.longitude}")
        toast("lokasi saat ini $loc")
    }

    fun showSettingsAlert(){
        dialog = alert("Can not get location", "Please enable GPS and Network") {
            yesButton {
                it.dismiss()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }.show()
    }

}