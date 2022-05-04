package com.example.ticktapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import java.io.IOException
import java.util.*


object SingleShotLocationGetProvider {
    @SuppressLint("MissingPermission")
    fun requestSingleUpdate(
        isCurrent: Boolean,
        context: Context,
        callback: LocationCallback
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isNetworkEnabled) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    try {
                        val gcd = Geocoder(context, Locale.getDefault())
                        val addresses: List<Address> = gcd.getFromLocation(
                            location.latitude,
                            location.longitude, 1
                        )
                        if (addresses.isNotEmpty()) {
                            val countryName = addresses[0].countryCode
                            if (isCurrent && countryName.equals("AU")) {
                                callback.onNewLocationAvailable(
                                    GPSCoordinates(
                                        location.latitude,
                                        location.longitude
                                    )
                                )
                            } else if (isCurrent && !countryName.equals("AU")) {
                                callback.onCurrentLocationNotFound()
                            } else if (!isCurrent) {
                                callback.onNewLocationAvailable(
                                    GPSCoordinates(
                                        location.latitude,
                                        location.longitude
                                    )
                                )
                            }
                        } else if (isCurrent) {
                            callback.onCurrentLocationNotFound()
                        } else if (!isCurrent) {
                            callback.onNewLocationAvailable(
                                GPSCoordinates(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }, null)
        } else {
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGPSEnabled) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        try {
                            val gcd = Geocoder(context, Locale.getDefault())
                            val addresses: List<Address> = gcd.getFromLocation(
                                location.latitude,
                                location.longitude, 1
                            )
                            if (addresses.isNotEmpty()) {
                                val countryName = addresses[0].countryCode
                                if (isCurrent && countryName.equals("AU")) {
                                    callback.onNewLocationAvailable(
                                        GPSCoordinates(
                                            location.latitude,
                                            location.longitude
                                        )
                                    )
                                } else if (isCurrent && !countryName.equals("AU")) {
                                    callback.onCurrentLocationNotFound()
                                } else if (!isCurrent) {
                                    callback.onNewLocationAvailable(
                                        GPSCoordinates(
                                            location.latitude,
                                            location.longitude
                                        )
                                    )
                                }
                            } else if (isCurrent) {
                                callback.onCurrentLocationNotFound()
                            } else if (!isCurrent) {
                                callback.onNewLocationAvailable(
                                    GPSCoordinates(
                                        location.latitude,
                                        location.longitude
                                    )
                                )
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }, null)
            }
        }
    }

    // consider returning Location instead of this dummy wrapper class
    class GPSCoordinates {
        var longitude = -1f
        var latitude = -1f

        constructor(theLatitude: Float, theLongitude: Float) {
            longitude = theLongitude
            latitude = theLatitude
        }

        constructor(theLatitude: Double, theLongitude: Double) {
            longitude = theLongitude.toFloat()
            latitude = theLatitude.toFloat()
        }
    }

    interface LocationCallback {
        fun onNewLocationAvailable(location: GPSCoordinates?)
        fun onCurrentLocationNotFound()
    }
}