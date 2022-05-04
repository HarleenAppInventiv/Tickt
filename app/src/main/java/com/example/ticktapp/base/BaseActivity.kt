package com.example.ticktapp.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MediaType
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.DialogPopupBinding
import com.example.ticktapp.dialog.LoadingDialog
import com.example.ticktapp.mvvm.view.LoginActivity
import com.example.ticktapp.snackbar.TSnackbar
import com.example.ticktapp.util.ResourceUtil
import com.googlelibrary.GoogleSignInAI
import com.googlelibrary.interfaces.GoogleSignOutCallback
import java.io.File
import java.net.URLConnection


/**
 * All activities will be extending this class in order to inherit basic feature
 * like showing [com.google.android.material.snackbar.Snackbar], [Toast] and [Dialog] etc..
 *
 */
abstract class BaseActivity : AppCompatActivity(), GoogleSignOutCallback,
    LiveDataObserver<ApiResponseData> {
    private lateinit var baseContainer: RelativeLayout
    private var progressDialog: LoadingDialog? = null
    private var baseViewModel: BaseViewModel? = null
    protected var outputUri: Uri? = null
    var mAppDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mAppDialog = Dialog(this)
        mAppDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mAppDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    /**
     * Remove status bar for using full screen
     */
    fun setFullScreen() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or 0
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.statusBarColor = Color.TRANSPARENT
    }

    fun changeStatusBarColor(colorId: Int) {
        val window = window
        //        clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //        add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //        finally change the color
        window.statusBarColor = ResourceUtil.getColor(colorId)
    }


    /**
     *
     * @param viewModel setting up the any [androidx.lifecycle.ViewModel] extending [BaseViewModel]
     * The main purpose to access any functionality for [BaseViewModel] like showing and hiding [LoadingDialog]
     *
     */
    fun setBaseViewModel(viewModel: BaseViewModel) {
        this.baseViewModel = viewModel
        baseViewModel?.loadingState?.observe(this, Observer { loadingState ->
            if (loadingState != null) {
                when (loadingState) {
                    is LoadingState.LOADING -> showProgressDialog(
                        loadingState.type,
                        loadingState.msg
                    )
                    is LoadingState.LOADED -> hideProgressDialog(
                        loadingState.type,
                        loadingState.msg
                    )
                }
            }
        })
    }


    /**
     * This will be called befor any [android.view.View.OnTouchListener]
     * This is balsically used to hide the keyboard when touching anywhere outside the keyboard bounds
     * @param ev identify the type of event
     * @return
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        if ((view != null) && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + view.getLeft() - scrcoords.get(0)
            val y: Float = ev.rawY + view.getTop() - scrcoords.get(1)
            if ((x < view.getLeft()) || (x > view.getRight()) || (y < view.getTop()) || (y > view.getBottom()))
                (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    (this.window.decorView.applicationWindowToken),
                    0
                )
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Method is used by the sub class for passing the id of the layout ot be inflated in the relative layout
     *
     * @return id of the resource to be inflated
     */
    fun getResourceId(): Int = 0

    /**
     * Common function to show common dialog[Dialog]
     *
     * @param desc description content
     * @param positiveText text for positive action
     * @param negativeText text for negative action
     * @param titleText title text that we want to show, if null or blank will be hidden
     * @param successCallBack lambda callback for positive action
     * @param cancelCallback lambda callback for negative action
     * @param shouldShowCancel show show negative action button False means hidden True means visible
     */
    fun showAppPopupDialog(
        desc: String,
        positiveText: String,
        negativeText: String,
        titleText: String = "",
        successCallBack: () -> Unit,
        cancelCallback: () -> Unit,
        shouldShowCancel: Boolean = true
    ) {
        if (mAppDialog?.isShowing == true)
            mAppDialog?.dismiss()
        val dialogView: DialogPopupBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_popup,
            null,
            false
        )
        mAppDialog?.setContentView(dialogView.root)
        mAppDialog?.setCancelable(false)
        mAppDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.tvMsg.text = desc
        dialogView.tvReject.text = negativeText
        dialogView.tvAccept.text = positiveText

        if (titleText.isEmpty()) {
            dialogView.tvTitle.visibility = View.GONE
        } else {
            dialogView.tvTitle.text = titleText
            dialogView.tvTitle.visibility = View.VISIBLE
        }
        if (!shouldShowCancel)
            dialogView.tvReject.visibility = View.GONE


        dialogView.tvAccept.setOnClickListener {
            successCallBack()
            mAppDialog?.dismiss()
        }
        dialogView.tvReject.setOnClickListener {
            cancelCallback()
            mAppDialog?.dismiss()
        }
        mAppDialog?.show()

    }

    /**
     * Show text for long duration
     *
     * @param message message contained in toast
     */
    fun showToastLong(message: CharSequence?) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        val view = toast.view
        try {
            view?.background?.setColorFilter(Color.parseColor("#606060"), PorterDuff.Mode.SRC_IN)
            val textView = toast.view!!.findViewById(android.R.id.message) as TextView
            textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            toast.show()
        } catch (e: Exception) {
            toast.show()
        }
    }

    /**
     * Show text for short duration
     * @param message message contained in toast
     */
    fun showToastShort(message: CharSequence?) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        val view = toast.view
        try {
            view?.background?.setColorFilter(Color.parseColor("#606060"), PorterDuff.Mode.SRC_IN)
            val textView = toast.view!!.findViewById(android.R.id.message) as TextView
            textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            toast.show()
        } catch (e: Exception) {
            toast.show()
        }
    }

    /**
     * showing the progress dialog
     *
     * @param type type of dialog or kind of sheemer effect
     * @param msg
     */
    open fun showProgressDialog(type: Int, msg: String) {
        if (!isDestroyed) {
            when (type) {
                LoaderType.NORMAL -> {
                    progressDialog = LoadingDialog(this)
                    progressDialog?.show()
                }
            }

        }
    }

    /**
     * hiding the progress dialog
     *
     * @param type type of dialog or kind of sheemer effect
     * @param msg
     */
    open fun hideProgressDialog(type: Int, msg: String) {
        when (type) {
            LoaderType.NORMAL -> {
                progressDialog?.let {
                    if (it.isShowing)
                        it.dismiss()
                }
            }
        }
    }

    /**
     *  Util function for replacing the existing fragment
     *
     * @param fragment fragment to be added
     * @param containerId id of container on which it will be loaded
     * @param isAddtoBackStack true if yes other false
     * @param tagName tag to identify the fragment transacation
     * @param bundle any data want to transfer to loaded fragment
     * @param isAnimated want animation for open and close fragment
     */
    protected fun replaceFragment(
        fragment: Fragment,
        containerId: Int,
        isAddtoBackStack: Boolean = false,
        tagName: String? = null,
        bundle: Bundle? = null,
        isAnimated: Boolean = false
    ) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (isAnimated)
            transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
        transaction.replace(containerId, fragment)
        if (isAddtoBackStack)
            transaction.addToBackStack(tagName)
        transaction.commit()
    }

    /**
     * remove currenlty laded fragment
     *
     * @param containerId
     */
    protected fun removeCurrentFragment(containerId: Int) {
        val fragment = supportFragmentManager.findFragmentById(containerId)
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.remove(fragment!!)
        fragmentTransaction.commit()

    }

    /**
     * Will be called when there is api success
     *
     * @param statusCode code to identify weather api succeed or fail
     * @param apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     * @param msg any error or success message
     */
    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        hideProgressDialog(LoaderType.NORMAL, "")
        when (apiCode) {
            ApiCodes.LOGIN -> {
                baseViewModel?.let {
                    finishAffinity()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        (application as ApplicationClass).refreshDataListener(refreshDataListener)
    }


    var refreshDataListener = object : ApplicationClass.OnRefreshDataListener {
        override fun refreshData(intent: Intent) {
            this@BaseActivity.refreshData(intent)
        }
    }

    open fun refreshData(newIntent: Intent) {

    }

    override fun onPause() {
        super.onPause()
        (application as ApplicationClass).refreshDataListener(null)
    }

    /**
     * called when there is any API error
     * @property exception observe any error in api
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    override fun onException(exception: ApiError, apiCode: Int) {
        hideProgressDialog(LoaderType.NORMAL, "")
        //showSnackBar(exception.message)
        showToastShort(exception.message)

        when (exception.status_code) {
            ApiCodes.ACCOUNT_EXPIRED -> {
                baseViewModel?.let {
                } ?: let {
                    val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
                    val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                    PreferenceManager.clearAllPrefs()
                    PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
                    PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
                    finishAffinity()
                    startActivity(Intent(this, LoginActivity::class.java))
                }

            }

        }

    }

    override fun onSessionExpired(exception: ApiError, apiCode: Int) {
        val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
        val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        PreferenceManager.clearAllPrefs()
        PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
        PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
        finishAffinity()
        startActivity(Intent(this, LoginActivity::class.java))

    }


    /**
     *called when there is no internet connection
     *
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     * @property msg any error or success messag
     */
    override fun noInternetConnection(apiCode: Int, msg: String?) {
        // showSnackBar(msg)
        showToastShort(msg)
        hideProgressDialog(LoaderType.NORMAL, "")
    }

    /**
     * Explicitly want to hide keyboard
     *
     * @param mEditText focused [EditText]
     */
    fun dismissKeyboard(mEditText: View) {
        try {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *  In order to show keyboard
     *
     */
    open fun showSoftKeyboard() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_IMPLICIT,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    open fun hideKeyboard() {
        try {
            val inputManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*
    * show the snack mar in for any success and fail
    * */
    fun showSnackBar(
        message: String?,
        backgroundColor: Int = ContextCompat.getColor(this, R.color.white),
        textColor: Int = ContextCompat.getColor(this, R.color.color_FF374E)
    ) {
        message?.let {
            val view: View = findViewById(android.R.id.content)
            val snackbar: TSnackbar = TSnackbar.make(view, it, TSnackbar.LENGTH_LONG)
            val snackbarView: View = snackbar.view
            snackbarView.setBackgroundColor(backgroundColor)
            val textView = snackbarView.findViewById<TextView>(R.id.snackbar_text)
            textView.setTextColor(textColor)
            snackbar.setMaxWidth(FrameLayout.LayoutParams.MATCH_PARENT)
            snackbar.setStatusBarHeight(0)
            snackbar.show()
        }
    }


    fun picImageFromCamera() {
        val getImage: File? = externalCacheDir
        if (getImage != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                outputUri = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    File(getImage.path, "IMG_" + System.currentTimeMillis() + ".jpeg")
                )
            } else {
                outputUri = Uri.fromFile(
                    File(
                        getImage.path,
                        "IMG_" + System.currentTimeMillis() + ".jpeg"
                    )
                )
            }
        }
        openCamera()
    }


    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        /*    val resInfoList = packageManager.queryIntentActivities(
                takePictureIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    outputUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

         */
        this.startActivityForResult(takePictureIntent, PermissionConstants.REQ_CAMERA)
    }


    fun onGalleryChoose(isMultiple: Boolean) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (isMultiple)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        this.startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.image_action)
            ), PermissionConstants.REQ_GALLERY
        )
    }

    fun onGalleryVideoChoose(isMultiple: Boolean) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (isMultiple)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/* video/*"
        this.startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.image_action)
            ), PermissionConstants.REQ_GALLERY
        )
    }

    fun onGalleryJustVideoChoose(isMultiple: Boolean) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (isMultiple)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "video/*"
        this.startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.image_action)
            ), PermissionConstants.REQ_GALLERY
        )
    }

    fun onGalleryJustImageChoose(isMultiple: Boolean) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (isMultiple)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        this.startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.image_action)
            ), PermissionConstants.REQ_GALLERY
        )
    }

    fun onVideoChoose() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        this.startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.image_action)
            ), PermissionConstants.REQ_VIDEO
        )
    }

    fun captureVideo() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15)
        this.startActivityForResult(takeVideoIntent, PermissionConstants.CAPTURE_VIDEO)
    }

    fun logoutFromGoogle() {
        val mGoogleSignInAI = GoogleSignInAI()
        mGoogleSignInAI.setActivity(this)
        mGoogleSignInAI.setSignOutCallback(this)
        mGoogleSignInAI.setUpGoogleClientForGoogleLogin()
        mGoogleSignInAI.doSignout()
    }

    override fun googleSignOutSuccessResult(message: String) {
        Log.d("Google Sign Out Resp: ", message)
    }

    override fun googleSignOutFailureResult(message: String) {
        Log.d("GoogleSignOut ErrResp: ", message)
    }

    fun appInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager = packageManager
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    fun getFileType(path: String?): Int {
        val mimeType: String = URLConnection.guessContentTypeFromName(path)
        return when {
            mimeType.startsWith("image") -> MediaType.IMAGE
            mimeType.startsWith("video") -> MediaType.VIDEO
            mimeType.equals("application/pdf") -> MediaType.PDF
            else -> MediaType.DOC
        }
    }


    fun isValidFile(path: String?, showVideo: Boolean = true): Boolean {
        if (path.isNullOrEmpty()) {
            return true
        } else {
//            val mimeType: String = URLConnection.guessContentTypeFromName(path)
//            return when {
//                mimeType.startsWith("image")
//                        || mimeType.startsWith("video")
//                        || mimeType.equals("application/pdf")
//                        || mimeType.equals("application/msword")
//                        || mimeType.equals("application/doc")
//                        || mimeType.equals("application/ms-doc")
//                        || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.documen")-> true
//                else -> false
//            }
            return when {
                path.endsWith(".pdf") || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(
                    ".doc"
                ) || path.endsWith(".docx") || path.endsWith(".jpeg") -> {
                    true
                }
                (path.endsWith(".mp4") || path.endsWith(".mov") || path.endsWith(".m4p") || path.endsWith(
                    ".m4v"
                ) && showVideo) -> true
                else -> {
                    false
                }
            }
        }
    }
}