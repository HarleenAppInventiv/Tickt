package com.example.ticktapp.mvvm.view.builder.postjob

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityLocationBinding
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.example.ticktapp.util.BottomSheetPermissionFragment
import com.example.ticktapp.util.SingleShotLocationProvider
import com.example.ticktapp.util.getPojoData
import com.example.ticktapp.util.toJsonString
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

class LocationActivity : BaseActivity() {
    private lateinit var mBinding: ActivityLocationBinding
    private var place: Place? = null
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var location: String = ""
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var data: JobRecModelRepublish? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        Places.initialize(this, getString(R.string.google_key))
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        getLocation()
    }

    private fun getIntentData() {
        if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_LOCATION)
                .isNullOrEmpty()
        ) {
            data = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                ?.getPojoData<JobRecModelRepublish>(JobRecModelRepublish::class.java)

            try {
                location =
                    PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_LOCATION) ?: ""
                mBinding.tvTimePicker.text = location

                lat = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_LAT)
                    ?.toDoubleOrNull() ?: 0.0
                lng = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_LONG)
                    ?.toDoubleOrNull() ?: 0.0


            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }else if (intent.hasExtra("data")) {
            data = intent.getSerializableExtra("data") as JobRecModelRepublish
            try {
                location = data?.location_name.toString()
                mBinding.tvTimePicker.text = location
                lat = data?.location?.location?.get(1)!!
                lng = data?.location?.location?.get(0)!!
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .isNullOrEmpty()
        ) {
            data = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                ?.getPojoData<JobRecModelRepublish>(JobRecModelRepublish::class.java)
            try {
                location =
                    data?.location_name ?: ""
                mBinding.tvTimePicker.text = location
                lat = data?.location?.location?.get(1)!!
                lng = data?.location?.location?.get(0)!!


            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    private fun getLocation() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                BottomSheetPermissionFragment(
                    this@LocationActivity,
                    object : BottomSheetPermissionFragment.OnPermissionResult {
                        override fun onPermissionAllowed() {
                            SingleShotLocationProvider.requestSingleUpdate(true,
                                this@LocationActivity,
                                object : SingleShotLocationProvider.LocationCallback {
                                    override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates?) {
                                    }

                                    override fun onCurrentLocationNotFound() {
                                    }
                                })
                        }

                        override fun onPermissionDenied() {
                        }
                    },
                    arrayOf(
                        BottomSheetPermissionFragment.ACCESS_COARSE_LOCATION,
                        BottomSheetPermissionFragment.ACCESS_FINE_LOCATION
                    )
                ).show(supportFragmentManager, "")
            }

        }, 1000)

    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun listener() {
        mBinding.postIvBack.setOnClickListener { onBackPressed() }
        mBinding.tvTimePicker.setOnClickListener {
            val fields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("AU")
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        mBinding.llCurrentTimePicker.setOnClickListener {
            BottomSheetPermissionFragment(
                this@LocationActivity,
                object : BottomSheetPermissionFragment.OnPermissionResult {
                    override fun onPermissionAllowed() {
                        SingleShotLocationProvider.requestSingleUpdate(true,
                            this@LocationActivity,
                            object : SingleShotLocationProvider.LocationCallback {
                                override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates?) {
                                    PreferenceManager.getString(PreferenceManager.LAT)
                                        ?.let { it1 ->
                                            PreferenceManager.getString(PreferenceManager.LAN)
                                                ?.let { it2 ->

                                                    lat = it1?.toDoubleOrNull() ?: 0.0
                                                    lng = it2?.toDoubleOrNull() ?: 0.0
                                                    mBinding.tvTimePicker.setText(
                                                        getCompleteAddressString(
                                                            it1.toDouble(),
                                                            it2.toDouble()
                                                        )
                                                    )

                                                    goToNextActivity()

//                                                startActivity(
//                                                    Intent(
//                                                        this@LocationActivity,
//                                                        JobDescActivity::class.java
//                                                    ).putExtra(
//                                                        "jobName",
//                                                        intent.getStringExtra("jobName")
//                                                    ).putExtra(
//                                                        "categories",
//                                                        intent.getSerializableExtra("categories")
//                                                    ).putExtra(
//                                                        "job_type",
//                                                        intent.getSerializableExtra("job_type")
//                                                    ).putExtra(
//                                                        "specialization",
//                                                        intent.getSerializableExtra("specialization")
//                                                    ).putExtra("lat", it1).putExtra("lng", it2)
//                                                        .putExtra(
//                                                            "location_name",
//                                                            getCompleteAddressString(
//                                                                it1.toDouble(),
//                                                                it2.toDouble()
//                                                            )
//                                                        )
//                                                )
                                                }
                                        }
                                }

                                override fun onCurrentLocationNotFound() {
                                    showToastShort(getString(R.string.no_services))
                                }
                            })
                    }

                    override fun onPermissionDenied() {
                        BottomSheetPermissionFragment.openSettingsDialog(this@LocationActivity)
                    }
                },
                arrayOf(
                    BottomSheetPermissionFragment.ACCESS_COARSE_LOCATION,
                    BottomSheetPermissionFragment.ACCESS_FINE_LOCATION
                )
            ).show(supportFragmentManager, "")
        }

        mBinding.tvLocationContinue.setOnClickListener {

            goToNextActivity()

            /*  if (place != null) {

                  PreferenceManager.putString(
                      PreferenceManager.NEW_JOB_PREF.JOB_LAT,
                      (place!!.latLng?.latitude).toString()
                  )
                  PreferenceManager.putString(
                      PreferenceManager.NEW_JOB_PREF.JOB_LONG,
                      (place!!.latLng?.longitude).toString()
                  )
                  PreferenceManager.putString(
                      PreferenceManager.NEW_JOB_PREF.JOB_LOCATION,
                      getPlaceName(place!!)
                  )

                  if (data != null) {
                      startActivity(
                          Intent(this, JobDescActivity::class.java)
                              .putExtra(
                                  "jobName",
                                  intent.getStringExtra("jobName")
                              ).putExtra(
                                  "categories",
                                  intent.getSerializableExtra("categories")
                              ).putExtra(
                                  "job_type",
                                  intent.getSerializableExtra("job_type")
                              ).putExtra(
                                  "specialization",
                                  intent.getSerializableExtra("specialization")
                              ).putExtra("lat", (place!!.latLng?.latitude).toString())
                              .putExtra("lng", (place!!.latLng?.longitude).toString())
                              .putExtra("location_name", getPlaceName(place!!))
                              .putExtra("data", data)
                      )

                  } else {
                      startActivity(
                          Intent(this, JobDescActivity::class.java)
                              .putExtra(
                                  "jobName",
                                  intent.getStringExtra("jobName")
                              ).putExtra(
                                  "categories",
                                  intent.getSerializableExtra("categories")
                              ).putExtra(
                                  "job_type",
                                  intent.getSerializableExtra("job_type")
                              ).putExtra(
                                  "specialization",
                                  intent.getSerializableExtra("specialization")
                              ).putExtra("lat", (place!!.latLng?.latitude).toString())
                              .putExtra("lng", (place!!.latLng?.longitude).toString())
                              .putExtra("location_name", getPlaceName(place!!))
                      )
                  }
              } else if (data != null) {
                  PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_LAT, lat.toString())
                  PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_LONG, lng.toString())
                  PreferenceManager.putString(
                      PreferenceManager.NEW_JOB_PREF.JOB_LOCATION,
                      mBinding.tvTimePicker.text.toString()
                  )
                  startActivity(
                      Intent(this, JobDescActivity::class.java)
                          .putExtra(
                              "jobName",
                              intent.getStringExtra("jobName")
                          ).putExtra(
                              "categories",
                              intent.getSerializableExtra("categories")
                          ).putExtra(
                              "job_type",
                              intent.getSerializableExtra("job_type")
                          ).putExtra(
                              "specialization",
                              intent.getSerializableExtra("specialization")
                          ).putExtra("lat", lat.toString())
                          .putExtra("lng", lng.toString())
                          .putExtra("location_name", mBinding.tvTimePicker.text.toString())
                          .putExtra("data", data)
                  )
              } else {
                  showToastShort(getString(R.string.please_enter_location))
              }*/
        }
    }

    private fun goToNextActivity() {
        if (lat == 0.0 || lng == 0.0) {
            showToastShort(getString(R.string.please_enter_location))
        } else {
            PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_LAT, lat.toString())
            PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_LONG, lng.toString())
            PreferenceManager.putString(
                PreferenceManager.NEW_JOB_PREF.JOB_LOCATION,
                mBinding.tvTimePicker.text.toString()
            )
            startActivity(
                Intent(this, JobDescActivity::class.java).apply {
                    if (this@LocationActivity.data != null) {
                        putExtra("data", this@LocationActivity.data)
                    }
                }
            )
        }
    }

    fun getCompleteAddressString(
        LATITUDE: Double,
        LONGITUDE: Double
    ): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.d("TAG", strReturnedAddress.toString())
            } else {
                Log.d("TAG", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TAG", "Canont get Address!")
        }
        return strAdd
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        place = Autocomplete.getPlaceFromIntent(data)
                        mBinding.tvTimePicker.text = getPlaceName(place!!)
                        lat = place?.latLng?.latitude ?: 0.0
                        lng = place?.latLng?.longitude ?: 0.0

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG", status.statusMessage.toString())
                    }
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getPlaceName(place: Place): String {
        if (place.name?.let { place.address?.contains(it) } == true) {
            return place.address.toString()
        }
        return place!!.name + ", " + place!!.address
    }
}