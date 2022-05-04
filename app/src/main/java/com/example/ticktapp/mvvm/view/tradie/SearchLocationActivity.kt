package com.example.ticktapp.mvvm.view.tradie

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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchLocationBinding
import com.app.core.model.jobmodel.JobModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.BottomSheetPermissionFragment
import com.example.ticktapp.util.SingleShotLocationProvider
import com.example.ticktapp.util.preventTwoClick
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*


public class SearchLocationActivity : BaseActivity() {
    private var isTradie: Boolean = false
    private var place: Place? = null
    private lateinit var mBinding: ActivitySearchLocationBinding
    private var data: JobModel? = null
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var location: String = ""
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_location)
        Places.initialize(this, getString(R.string.google_key))
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setupView()
        listener()
        getLocation()
    }

    private fun getLocation() {
        Handler().postDelayed(object : Runnable {
                override fun run() {
                    BottomSheetPermissionFragment(
                        this@SearchLocationActivity,
                        object : BottomSheetPermissionFragment.OnPermissionResult {
                            override fun onPermissionAllowed() {
                                SingleShotLocationProvider.requestSingleUpdate(true,
                                    this@SearchLocationActivity,
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

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }


    fun getIntentData() {
        if (intent.hasExtra("isTradie")) {
            isTradie = intent.getBooleanExtra("isTradie", false)
        }
        if (intent.hasExtra("data")) {
            data = intent.getSerializableExtra("data") as JobModel
        }
        if (intent.hasExtra("lat")) {
            lat = intent.getDoubleExtra("lat", 0.0)
            lng = intent.getDoubleExtra("lng", 0.0)
            location = intent.getStringExtra("location").toString()
            if (location != getString(R.string.select_place)) {
                mBinding.tvTimePicker.text = location
            }
        }
        if (isTradie) {
            mBinding.tvSearchTitle.text = getString(R.string.where_is_your_job_)
            mBinding.tvSearchTitle.setTextColor(ContextCompat.getColor(this, R.color.color_161d4a))
        }
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setupView() {
        if (data != null) {
            mBinding.llHeader.visibility = View.VISIBLE
            mBinding.tvSkip.visibility = View.VISIBLE
            mBinding.tvTitle.text = data?.name
            mBinding.tvDetails.text = data?.trade_name
            Glide.with(mBinding.root.context).load(data?.image)
                .into(mBinding.ivUserProfile)
        } else {
            mBinding.tvSkip.visibility = View.GONE
            mBinding.llHeader.visibility = View.GONE
        }
    }

    private fun listener() {
        permissionHelper = PermissionHelper()
        mBinding.searchToolbarBack.setOnClickListener { onBackPressed() }
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
                this@SearchLocationActivity,
                object : BottomSheetPermissionFragment.OnPermissionResult {
                    override fun onPermissionAllowed() {
                        SingleShotLocationProvider.requestSingleUpdate(true,
                            this@SearchLocationActivity,
                            object : SingleShotLocationProvider.LocationCallback {
                                override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates?) {
                                    PreferenceManager.getString(PreferenceManager.LAT)
                                        ?.let { it1 ->
                                            PreferenceManager.getString(PreferenceManager.LAN)
                                                ?.let { it2 ->
                                                    {
                                                        if (data != null) {
                                                            startActivity(
                                                                Intent(
                                                                    this@SearchLocationActivity,
                                                                    SearchCalendarActivity::class.java
                                                                ).putExtra("data", data)
                                                                    .putExtra(
                                                                        "isTradie",
                                                                        intent.getBooleanExtra(
                                                                            "isTradie",
                                                                            false
                                                                        )
                                                                    )
                                                                    .putExtra(
                                                                        "isSearchType",
                                                                        intent.getIntExtra(
                                                                            "isSearchType",
                                                                            0
                                                                        )
                                                                    )
                                                                    .putExtra(
                                                                        "amount",
                                                                        intent.getStringExtra("amount")
                                                                    )
                                                                    .putExtra("lat", it1.toDouble())
                                                                    .putExtra("lng", it2.toDouble())
                                                                    .putExtra(
                                                                        "location",
                                                                        getCompleteAddressString(
                                                                            it1.toDouble(),
                                                                            it2.toDouble()
                                                                        )
                                                                    )
                                                            )
                                                        } else {
                                                            val intent = Intent()
                                                                .putExtra("lat", it1.toDouble())
                                                                .putExtra("lng", it2.toDouble())
                                                                .putExtra(
                                                                    "location",
                                                                    getCompleteAddressString(
                                                                        it1.toDouble(),
                                                                        it2.toDouble()
                                                                    )
                                                                )
                                                            setResult(
                                                                Activity.RESULT_OK,
                                                                intent
                                                            )
                                                            finish()
                                                        }
                                                    }

                                                }
                                        }
                                }

                                override fun onCurrentLocationNotFound() {
                                    showToastShort(getString(R.string.no_services))
                                }
                            })
                    }

                    override fun onPermissionDenied() {
                        BottomSheetPermissionFragment.openSettingsDialog(this@SearchLocationActivity)
                    }
                },
                arrayOf(
                    BottomSheetPermissionFragment.ACCESS_COARSE_LOCATION,
                    BottomSheetPermissionFragment.ACCESS_FINE_LOCATION
                )
            ).show(supportFragmentManager, "")
        }


        mBinding.tvSkip.setOnClickListener {
            preventTwoClick(mBinding.tvSkip)
            startActivity(
                Intent(this, SearchCalendarActivity::class.java).putExtra("data", data)
                    .putExtra("isTradie", intent.getBooleanExtra("isTradie", false))
                    .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                    .putExtra("amount", intent.getStringExtra("amount"))
            )
        }
        mBinding.tvContinue.setOnClickListener {
            if (place != null) {
                if (data != null) {
                    startActivity(
                        Intent(this, SearchCalendarActivity::class.java)
                            .putExtra("isTradie", intent.getBooleanExtra("isTradie", false))
                            .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                            .putExtra("amount", intent.getStringExtra("amount"))
                            .putExtra("lat", place!!.latLng?.latitude)
                            .putExtra("lng", place!!.latLng?.longitude)
                            .putExtra("location", getPlaceName(place!!))
                            .putExtra("data", this.data)
                    )
                } else {
                    val intent = Intent()
                        .putExtra("lat", place!!.latLng?.latitude)
                        .putExtra("lng", place!!.latLng?.longitude)
                        .putExtra("location", getPlaceName(place!!))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } else if (location.isNotEmpty() && location != getString(R.string.select_place)) {
                val intent = Intent()
                    .putExtra("lat", lat)
                    .putExtra("lng", lng)
                    .putExtra("location", location)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                showToastShort(getString(R.string.please_select_location))
            }
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