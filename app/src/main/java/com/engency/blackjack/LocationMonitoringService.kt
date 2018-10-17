package com.engency.blackjack

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.network.OnNetworkResponseInterface
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import org.json.JSONObject


class LocationMonitoringService : Service(),
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private val locationInterval: Long = 5 * 60
    private val fastestLocationInterval: Long = 4 * 60
    private val TAG = LocationMonitoringService::class.java.simpleName
    private lateinit var mLocationClient: GoogleApiClient
    private var mLocationRequest = LocationRequest()
    private lateinit var properties: GroupPropertyManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mLocationClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationRequest.interval = locationInterval
        mLocationRequest.fastestInterval = fastestLocationInterval

        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationClient.connect()

        this.properties = GroupPropertyManager(applicationContext)

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /*
     * LOCATION CALLBACKS
     */
    override fun onConnected(dataBundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "== Error On onConnected() Permission not granted")

            return
        }

        if (shouldPollLocation()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val locationList = locationResult.locations
                    if (locationList.size > 0) {
                        //The last location in the list is the newest
                        val location = locationList[locationList.size - 1]
                        submitLocation(location.latitude, location.longitude)
                    }
                }
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

            Log.d(TAG, "Connected to Google API")
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, "Connection suspended")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "Failed to connect to Google API")

    }

    private fun submitLocation(lat: Double, lon: Double) {
        if (!shouldPollLocation()) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            return
        }

        Log.i(TAG, "Location: " + lat + " " + lon)

        NetworkHelper.submitLocation(lat, lon, properties.get("token")!!, object : OnNetworkResponseInterface {
            override fun success(data: JSONObject) {
                Log.d(TAG, "Location submitted")
            }

            override fun failure(message: String) {
                Log.d(TAG, "Failure while submitting location")
            }

        })

    }

    private fun shouldPollLocation(): Boolean {
        return properties.has("token") && properties.has("pushLocation") && properties.get("pushLocation") == "1"
    }
}