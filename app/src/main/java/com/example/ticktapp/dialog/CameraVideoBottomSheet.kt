package com.example.ticktapp.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.databinding.BottomSheetVideoBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Bottom sheet dialog for options for camera
 *
 * @property mCameraDialogCallBack callback for observing events in dialog
 * @property shouldShowRemoveOption weather to show remove option in camear dialog
 *
 * @param context
 */
class CameraVideoBottomSheet(
    context: Context,
    var mCameraDialogCallBack: CameraDialogCallBack
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme), View.OnClickListener {
    lateinit var mBinding: BottomSheetVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.bottom_sheet_video,
            null,
            false
        )
        setContentView(mBinding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setListeners()
    }

    private fun setListeners() {
        mBinding.tvCamera.setOnClickListener(this)
        mBinding.tvGallery.setOnClickListener(this)
        mBinding.tvFiles.setOnClickListener(this)
        mBinding.tvGallery.setBackgroundResource(R.drawable.bg_btn_pale)
        mBinding.tvFilePicker.setVisibility(View.GONE)
    }


    override fun onClick(v: View?) {
        dismiss()
        when (v) {
            mBinding.tvGallery -> mCameraDialogCallBack.onGalleryClicked()
            mBinding.tvCamera -> mCameraDialogCallBack.onCameraClicked()
            mBinding.tvFiles -> mCameraDialogCallBack.onVideoCapture()
            mBinding.tvFilePicker -> mCameraDialogCallBack.onFileClicked()
        }
    }


    /**
     * Callback that types of events emit by the interface
     *
     */
    interface CameraDialogCallBack {
        fun onCameraClicked() {}
        fun onGalleryClicked() {}
        fun onVideoCapture() {}
        fun onRemoveClicked() {}
        fun onFileClicked() {}
    }


}