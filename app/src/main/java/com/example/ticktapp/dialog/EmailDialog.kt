package com.example.ticktapp.dialog

import CoreUtils
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.databinding.DialogEmailBinding

/**
 * This dailog is used ask user to enter email while inviting contacts when there is no email for a contact
 *
 * @property callback lambda callback to observe yser actions
 * @constructor
 *
 * @param context activity's context
 */
class EmailDialog(context: Context, val callback: (String) -> Unit) : Dialog(context) {

    private lateinit var mBinding: DialogEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_email,
            null,
            false
        )
        setContentView(mBinding.root)
        setCancelable(true)
        window!!.setLayout(
            context.resources.displayMetrics.widthPixels - context.resources.getDimension(R.dimen._100sdp)
                .toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBinding.tvBlueButton.tvBlueBtn.text = SpannableStringBuilder(context.getString(R.string.invite))
        setListeners()
    }

    private fun setListeners() {
        mBinding.edtEmail.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                mBinding.tvEmailError.isVisible = false
        }
        mBinding.tvBlueButton.tvBlueBtn.setOnClickListener {
            mBinding.edtEmail.clearFocus()
            mBinding.tvEmailError.isVisible = false
            if (mBinding.edtEmail.text.isNullOrBlank()) {
                mBinding.tvEmailError.isVisible = true
                mBinding.tvEmailError.text = ApplicationClass.applicationContext().getString(
                    R.string.please_enter_email_address
                )
            } else if (!CoreUtils.isEmailValid(mBinding.edtEmail.text.toString())) {
                mBinding.tvEmailError.isVisible = true
                mBinding.tvEmailError.text = ApplicationClass.applicationContext().getString(
                    R.string.email_is_not_valid
                )

            } else {
                callback(mBinding.edtEmail.text.toString())
                dismiss()
            }
        }
    }
}