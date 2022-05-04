package com.example.ticktapp.mvvm.view.tradie

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.FragmentBuilderProfileBinding
import com.example.ticktapp.dialog.GalleryCameraBottomSheet
import com.example.ticktapp.mvvm.view.WelcomeActivity
import com.example.ticktapp.mvvm.view.builder.BuilderMyRevenueActivity
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.builder.TradieListActivity
import com.example.ticktapp.mvvm.view.builder.WebViewActivity
import com.example.ticktapp.mvvm.view.builder.postjob.MilestoneTemplateActivity
import com.example.ticktapp.mvvm.view.builder.profile.BuilderSettingsActivity
import com.example.ticktapp.mvvm.view.builder.profile.EditBuilderBasicDetailsActivity
import com.example.ticktapp.mvvm.view.builder.profile.MyBuilderProfileActivity
import com.example.ticktapp.mvvm.view.builder.profile.PaymentSelectActivity
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.mvvm.viewmodel.ProfileViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.example.ticktapp.util.MoEngageUtils
import com.google.gson.JsonObject
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.yalantis.ucrop.UCrop
import io.intercom.android.sdk.Intercom
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class ProfileOfBuilderFragment : BaseFragment(), View.OnClickListener,
    PermissionHelper.IGetPermissionListener, GalleryCameraBottomSheet.CameraDialogCallBack,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mBinding: FragmentBuilderProfileBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(BaseViewModel::class.java) }
    private val mProfileViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }
    private lateinit var permissionHelper: PermissionHelper
    private var uri: Uri? = null
    private var fPAth: String? = null
    private val mUplodaViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }

    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )

    companion object {
        fun getInstance(): ProfileOfBuilderFragment {
            val fragment = ProfileOfBuilderFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_builder_profile, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    override fun initialiseFragmentBaseViewModel() {
        setObservers()
        setListener()
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
        mBinding.ivCam.bringToFront()
        mProfileViewModel.getInitialProfileData(true)
    }

    private fun setObservers() {
        setFragmentBaseViewModel(mProfileViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mProfileViewModel.getResponseObserver().observe(this, this)
        mUplodaViewModel.getResponseObserver().observe(this, this)
    }

    private fun setListener() {
        mBinding.tvLogout.setOnClickListener {
            setFragmentBaseViewModel(mViewModel)
            initialiseLiveData()
            logOut()
        }
        mBinding.ivCam.setOnClickListener(this)
        mBinding.tvViewProfile.setOnClickListener(this)
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tvComplete.setOnClickListener(this)
        mBinding.tvPersonalInfo.setOnClickListener(this)
        mBinding.tvMilestoneTemplate.setOnClickListener(this)
        mBinding.tvSavedJobs.setOnClickListener(this)
        mBinding.tvPrivacy.setOnClickListener(this)
        mBinding.tvTerms.setOnClickListener(this)
        mBinding.tvSetting.setOnClickListener(this)
        mBinding.tvBankingDetails.setOnClickListener(this)
        mBinding.tvMyPayments.setOnClickListener(this)
        mBinding.tvSupportChat.setOnClickListener(this)
        mBinding.tvAppGuide.setOnClickListener(this)
    }

    fun logOut() {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.dialog_popup)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
        title.text = getString(R.string.logout)

        val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
        msg.text = getString(R.string.are_you_sure_you_want_logout)

        val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
        dialogBtn_okay.text = getString(R.string.ok)
        dialogBtn_okay.setOnClickListener {
            dialog.dismiss()
            mViewModel.doLogout()
        }

        val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
        dialogBtn_cancel.text = getString(R.string.cancel)
        dialogBtn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.INIT_BASIC_PROFILE, ApiCodes.LOGOUT, ApiCodes.UPLOAD_FILE, ApiCodes.BASIC_PROFILE_IMG -> {
                showToastShort(exception.message)
            }
        }
        super.onException(exception, apiCode)
    }

    override fun authorizeFailed(apiCode: Int) {
        val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
        val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        PreferenceManager.clearAllPrefs()
        PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
        PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
        startActivity(Intent(activity, WelcomeActivity::class.java))
        activity?.finish()
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.LOGOUT -> {
                clearNotifications()
                logoutUserOnMoEngage()
                logoutMixPanel()
                val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
                val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                PreferenceManager.clearAllPrefs()
                PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
                PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
                startActivity(Intent(activity, WelcomeActivity::class.java))
                activity?.finish()
            }

            ApiCodes.INIT_BASIC_PROFILE -> {
                mBinding.model = mProfileViewModel.inItProfileModel
                PreferenceManager.putString(
                    PreferenceManager.NAME,
                    mProfileViewModel.inItProfileModel.userName
                )
                PreferenceManager.putString(
                    PreferenceManager.PROFILE_IMAGE,
                    mProfileViewModel.inItProfileModel.userImage
                )
                mBinding.progressBarHor.max = 100
                mBinding.progressBarHor.progress =
                    mProfileViewModel.inItProfileModel.profileCompleted.split("%").get(0)
                        .toInt()
                Glide.with(mBinding.root.context)
                    .load(mProfileViewModel.inItProfileModel.userImage)
                    .placeholder(R.drawable.ic_user_place_holder)
                    .error(R.drawable.ic_user_place_holder)
                    .into(mBinding.ivUserProfile)
            }
            ApiCodes.UPLOAD_FILE -> {
                mUplodaViewModel.imageUploadResponse.url?.let {
                    if (it.isNotEmpty()) {
                        val mObject = JsonObject()
                        mObject.addProperty("userImage", it.get(0))
                        mProfileViewModel.uploadProfileImg(mObject)

                    }
                }
            }
            ApiCodes.BASIC_PROFILE_IMG -> {
                Glide.with(mBinding.root.context)
                    .load(fPAth)
                    .placeholder(R.drawable.ic_user_place_holder)
                    .error(R.drawable.ic_user_place_holder)
                    .into(mBinding.ivUserProfile)
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    fun clearNotifications(){
        val notificationManager: NotificationManager =
            requireActivity().getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.ivCam -> {
                if (permissionHelper.hasPermissionInFragment(
                        requireActivity(), this,
                        permissions, PermissionConstants.PERMISSION
                    )
                ) {

                    GalleryCameraBottomSheet(requireContext(), this).show()
                }
            }
            mBinding.tvViewProfile -> {
                val intent = Intent(requireContext(), MyBuilderProfileActivity::class.java)
                resultLauncher.launch(intent)

            }
            mBinding.tvPersonalInfo -> {
                val intent = Intent(requireContext(), MyBuilderProfileActivity::class.java)
                intent.putExtra("isEditable", true)
                resultLauncher.launch(intent)
            }
            mBinding.tvComplete -> {
                val intent = Intent(requireContext(), EditBuilderBasicDetailsActivity::class.java)
                resultLauncher.launch(intent)
            }
            mBinding.tvMilestoneTemplate -> {
                startActivity(
                    Intent(
                        requireContext(),
                        MilestoneTemplateActivity::class.java
                    ).putExtra("fromProfile", true)
                )
            }
            mBinding.tvSavedJobs -> {
                startActivity(
                    Intent(
                        requireContext(),
                        TradieListActivity::class.java
                    ).putExtra("title", getString(R.string.saved_tradie_))
                )
            }
            mBinding.tvPrivacy -> {
                startActivity(
                    Intent(activity, WebViewActivity::class.java)
                        .putExtra(IntentConstants.FROM, Constants.PRIVACY)
                )
            }
            mBinding.tvTerms -> {
                startActivity(
                    Intent(activity, WebViewActivity::class.java)
                        .putExtra(IntentConstants.FROM, Constants.TERMS)
                )
            }

            mBinding.tvSetting -> {
                startActivity(
                    Intent(activity, BuilderSettingsActivity::class.java)
                )
            }

            mBinding.tvBankingDetails -> {
                startActivity(
                    Intent(activity, PaymentSelectActivity::class.java)
                )
            }
            mBinding.tvMyPayments -> {
                startActivity(
                    Intent(activity, BuilderMyRevenueActivity::class.java)
                )
            }
            mBinding.tvSupportChat -> {
                Intercom.client().displayMessenger()
            }
            mBinding.tvAppGuide -> {
                if (activity is HomeBuilderActivity) {
                    (activity as HomeBuilderActivity).setUpTutorial()
                }
            }
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setFragmentBaseViewModel(mProfileViewModel)
                initialiseLiveData()
                mProfileViewModel.getInitialProfileData(true)
            }
        }

    override fun permissionGiven(requestCode: Int) {
        when (requestCode) {
            PermissionConstants.PERMISSION ->
                GalleryCameraBottomSheet(requireActivity(), this).show()
        }
    }

    override fun permissionCancel(requestCode: Int) {
    }

    override fun onCameraClicked() {
        if (permissionHelper.hasPermission(
                requireActivity(),
                permissions,
                PermissionConstants.REQ_CAMERA
            )
        ) {
            openCamera()

        }
    }

    override fun onGalleryClicked() {
        if (permissionHelper.hasPermission(
                requireActivity(),
                permissions,
                PermissionConstants.REQ_CAMERA
            )
        ) {
            onGalleryJustImageChoose(false)

        }
    }

    override fun onVideoCapture() {

    }

    override fun onFileClicked() {


    }


    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, PermissionConstants.REQ_CAMERA)
    }

    fun onGalleryJustImageChoose(isMultiple: Boolean) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (isMultiple)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setType("image/*")
        startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.image_action)
            ), PermissionConstants.REQ_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            PermissionConstants.REQ_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    uri = data.data
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(requireActivity(), it) }
//                        var destinationFileName = System.currentTimeMillis().toString() + ""
//                        destinationFileName += ".jpeg"
//                        val file = File(requireActivity().cacheDir, destinationFileName)
//                        if (!file.exists()) {
//                            try {
//                                file.createNewFile()
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                        }
//                        UCrop.of(
//                            Uri.fromFile(File(fPAth)), Uri.fromFile(
//                                File(
//                                    requireActivity().cacheDir, destinationFileName
//                                )
//                            )
//                        ).withAspectRatio(1f, 1f)
//                            .start(requireContext(), this)

                        fPAth?.let {
                            setFragmentBaseViewModel(mUplodaViewModel)
                            initialiseLiveData()
                            mUplodaViewModel.hitUploadFile(fPAth!!)
                        }
                    }
                }
            }
            PermissionConstants.REQ_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(requireActivity(), it) }
//                        var destinationFileName = System.currentTimeMillis().toString() + ""
//                        destinationFileName += ".jpeg"
//                        val file = File(requireActivity().cacheDir, destinationFileName)
//                        if (!file.exists()) {
//                            try {
//                                file.createNewFile()
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                        }
//                        UCrop.of(
//                            Uri.fromFile(File(fPAth)), Uri.fromFile(
//                                File(
//                                    requireActivity().cacheDir, destinationFileName
//                                )
//                            )
//                        ).withAspectRatio(1f, 1f)
//                            .start(requireContext(), this)
                        fPAth?.let {
                            setFragmentBaseViewModel(mUplodaViewModel)
                            initialiseLiveData()
                            mUplodaViewModel.hitUploadFile(fPAth!!)
                        }   }
                }
            }


            UCrop.REQUEST_CROP -> {
                if (data != null) {
                    val resultUri = UCrop.getOutput(data!!)
                    if (resultUri != null) fPAth = resultUri.path
                    else
                        return
                    fPAth?.let {
                        setFragmentBaseViewModel(mUplodaViewModel)
                        initialiseLiveData()
                        mUplodaViewModel.hitUploadFile(fPAth!!)
                    }

                }
            }
        }

    }

    private fun getImageUri(photo: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            requireActivity().getContentResolver(),
            photo,
            "Title",
            null
        )
        return Uri.parse(path)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.setPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onRefresh() {
        mProfileViewModel.getInitialProfileData(false)
    }

    private fun logoutUserOnMoEngage() {
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.SUCCESS_STATUS, true)
        signUpProperty.addAttribute(MoEngageConstants.CURRENT_PAGE, "Profile")

        MoEngageUtils.sendEvent(
            requireActivity(),
            MoEngageConstants.MOENGAGE_EVENT_LOG_OUT,
            signUpProperty
        )

        //logout from MO-Engage SDK
        MoEHelper.getInstance(requireActivity()).logoutUser()
    }

    private fun logoutMixPanel(){
        val mixpanel = MixpanelAPI.getInstance(
            requireContext(),
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.SUCCESS_STATUS, true)
        props.put(MoEngageConstants.CURRENT_PAGE, "Profile")

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_LOG_OUT, props)
    }
}
