package majed.eddin.weatherapp.utils.extentionUtils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import majed.eddin.weatherapp.data.consts.Params

fun Context.getLocationWithCheckNetworkAndGPS(): Location? {
    val lm: LocationManager =
        (this.getSystemService(Context.LOCATION_SERVICE))!! as LocationManager

    val isGpsEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkLocationEnabled: Boolean =
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    var networkLoacation: Location? = null
    val gpsLocation: Location?
    var finalLoc: Location? = null

    if (isGpsEnabled)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this as Activity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), Params.PERMISSIONS_LOCATION
            )

            return null
        }

    gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    if (isNetworkLocationEnabled)
        networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

    if (gpsLocation != null && networkLoacation != null) {

        //smaller the number more accurate result will
        return if (gpsLocation.accuracy > networkLoacation.accuracy) {
            finalLoc = networkLoacation
            finalLoc
        } else {
            finalLoc = gpsLocation
            finalLoc
        }

    } else {

        if (gpsLocation != null) {
            finalLoc = gpsLocation
            return finalLoc
        } else if (networkLoacation != null) {
            finalLoc = networkLoacation
            return finalLoc
        }
    }

    return finalLoc
}