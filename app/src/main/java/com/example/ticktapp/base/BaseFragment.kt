package com.example.ticktapp.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.google.android.material.textfield.TextInputLayout
import com.app.core.util.ApiError
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.dialog.LoadingDialog
import com.example.ticktapp.mvvm.view.LoginActivity
import com.example.ticktapp.util.MyPasswordTransformationMethod
import java.io.File
import java.io.IOException

/**
 * All fragments will be extending this class in order to inherit basic feature
 * like showing [com.google.android.material.snackbar.Snackbar], [Toast] and [Dialog] etc..
 *
 */
abstract class BaseFragment : Fragment(), LiveDataObserver<ApiResponseData> {
    private var progressDialog: LoadingDialog? = null
    private var fragmentBaseViewModel: BaseViewModel? = null
    abstract fun initialiseFragmentBaseViewModel()
    protected var outputUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialiseFragmentBaseViewModel()
        initialiseLiveData()
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun initialiseLiveData() {
        fragmentBaseViewModel?.loadingState?.observe(this@BaseFragment.viewLifecycleOwner, Observer { loadingState ->
            if (loadingState != null) {
                when (loadingState) {
                    is LoadingState.LOADING -> showProgressDialog(loadingState.type,loadingState.msg)
                    is LoadingState.LOADED -> hideProgressDialog(loadingState.type,loadingState.msg)
                }
            }
        })
    }


    /**
     *
     * @param viewModel setting up the any [androidx.lifecycle.ViewModel] extending [BaseViewModel]
     * The main purpose to access any functionality for [BaseViewModel] like showing and hiding [LoadingDialog]
     *
     */
    protected fun setFragmentBaseViewModel(viewModel: BaseViewModel){
        this.fragmentBaseViewModel = viewModel
    }


    /**
     * Show text for long duration
     *
     * @param message message contained in toast
     */
    fun showToastLong(message: CharSequence?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Show text for short duration
     *
     * @param message message contained in toast
     */
    fun showToastShort(message: CharSequence?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * showing the progress dialog
     *
     * @param type type of dialog or kind of sheemer effect
     * @param msg
     */
    open fun showProgressDialog(type: Int, msg: String) {
        if(progressDialog == null){
            progressDialog = LoadingDialog(requireContext())
        }
        when(type){
            LoaderType.NORMAL -> {
                progressDialog?.let {
                    if(!it.isShowing)
                        it.show()
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
        when(type){
            LoaderType.NORMAL -> {
                progressDialog?.let {
                    if (it.isShowing)
                        it.dismiss()
                }
            }
        }
    }


    protected fun setTransformatiomMethod(edInput : EditText, edInputLayout : TextInputLayout){
        edInput.transformationMethod = MyPasswordTransformationMethod()
        passwordToggleButton(edInputLayout)
    }
    protected fun passwordToggleButton(edInputLayout : TextInputLayout){
        edInputLayout.setEndIconOnClickListener {
            val editText: EditText? = edInputLayout.editText
            val selection = editText?.selectionEnd ?: 0
            val hasPassTransformationMethod =
                editText?.transformationMethod is MyPasswordTransformationMethod

            if(hasPassTransformationMethod){
                editText?.transformationMethod = null
            }else{
                editText?.transformationMethod = MyPasswordTransformationMethod()
            }
            editText?.setSelection(selection)
        }
    }

    /**
     * called when there is any API error
     * @property exception observe any error in api
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    override fun onException(exception: ApiError, apiCode: Int) {
//
    }

    override fun onSessionExpired(exception: ApiError, apiCode: Int) {
        val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
        val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        PreferenceManager.clearAllPrefs()
        PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
        PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
        activity?.finishAffinity()
        startActivity(Intent(activity, LoginActivity::class.java))

    }


    /**
     * Will be called when there is api success
     *
     * @param statusCode code to identify weather api succeed or fail
     * @param apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     * @param msg any error or success message
     */
    override fun onResponseSuccess(statusCode : Int, apiCode: Int, msg: String?) {
//        fragmentBaseViewModel?.setLoadingState(LoadingState.LOADED)
    }

    /**
     *called when there is no internet connection
     *
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     * @property msg any error or success messag
     */
    override fun noInternetConnection(apiCode: Int, msg: String?) {
//        fragmentBaseViewModel?.setLoadingState(LoadingState.LOADED)
        showToastLong(msg)
    }


    fun dismissKeyboard(mEditText : View,activity : Activity){
        try {
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    open fun showSoftKeyboard(activity: Activity) {
        val imm =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

}