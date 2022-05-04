package com.example.ticktapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.ticktapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A [BottomSheetDialogFragment]
 */
class BottomSheetPermissionFragment(
    private val appCompatActivity: AppCompatActivity,
    private val onPermissionResult: OnPermissionResult,
    private val requiredPermission: Array<String?>?
) : BottomSheetDialogFragment() {
    private val PERMISSION_REQUEST_CODE = 1

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView: View = LinearLayout(appCompatActivity)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0f)
        dialog.setContentView(contentView)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermission(): Boolean {
        return if (hasSelfPermission(
                appCompatActivity as Activity,
                requiredPermission
            )
        ) {
            dismissAllowingStateLoss()
            onPermissionResult.onPermissionAllowed()
            true
        } else {
            requestPermissions(requiredPermission!!, PERMISSION_REQUEST_CODE)
            false
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    interface OnPermissionResult {
        fun onPermissionAllowed()
        fun onPermissionDenied()
    }

    override fun onResume() {
        super.onResume()
        if (requiredPermission != null && requiredPermission.size > 0) {
            checkPermission()
        } else {
            onPermissionResult.onPermissionAllowed()
            dismissAllowingStateLoss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var hadAllowed = true
            for (grantResult in grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    hadAllowed = false
                    break
                }
            }
            if (hadAllowed) {
                dismissAllowingStateLoss()
                onPermissionResult.onPermissionAllowed()
            } else {
                dismissAllowingStateLoss()
                onPermissionResult.onPermissionDenied()
            }
        }
    }

    companion object {
        const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val CAMERA = Manifest.permission.CAMERA
        const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        const val CALL_PHONE = Manifest.permission.CALL_PHONE

        /**
         * Returns true if the Activity has access to all given permissions.
         * Always returns true on platforms below M.
         *
         * @see Activity.checkSelfPermission
         */
        @TargetApi(Build.VERSION_CODES.M)
        fun hasSelfPermission(activity: Activity, permissions: Array<String?>?): Boolean {
            // Below Android M all permissions are granted at install time and are already available.
            if (!isMNC) {
                return true
            }

            // Verify that all required permissions have been granted
            for (permission in permissions!!) {
                if (activity.checkSelfPermission(permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }

        /*
         the codename, not the version code. Once the API has been finalised, the following check
         should be used: */
        //return "MNC".equals(Build.VERSION.CODENAME);
        val isMNC: Boolean
            get() =/*
                    the codename, not the version code. Once the API has been finalised, the following check
                    should be used: */
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        //return "MNC".equals(Build.VERSION.CODENAME);
        fun openSettingsDialog(context: Context) {
            val ald = AlertDialog.Builder(context)
            ald.setMessage(context.resources.getString(R.string.allow_permission_settings))
            ald.setPositiveButton(context.resources.getString(R.string.settings)) { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            }
            ald.setNegativeButton(context.resources.getString(R.string.cancel), null)
            ald.show()
        }
    }


}