package com.example.ticktapp.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.databinding.DialogMonthPickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by Sachin on 19/6/21.
 * Prismetric Technology, Gandhinagar, Gujarat
 */
class SelectRecommandedHours(
    context: Context,
    val hours: String,
    val listener: View.OnClickListener
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme), View.OnClickListener {
    lateinit var mBinding: DialogMonthPickerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_month_picker,
            null,
            false
        )
        setContentView(mBinding.getRoot())
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mBinding.dialogMonthPickerPickerTvHour.value = 0
        mBinding.dialogMonthPickerPickerTvMinute.value = 0
        mBinding.dialogTvNo.setOnClickListener(this)
        mBinding.dialogTvOk.setOnClickListener(this)
        mBinding.dialogMonthPickerPickerTvMinute.setFormatter(object : NumberPicker.Formatter {
            override fun format(p0: Int): String {
                return (p0 * 15).toString()
            }
        })
        if (hours.length > 0) {
            val hour = hours.split(":")
            mBinding.dialogMonthPickerPickerTvHour.value = hour[0].toInt()
            mBinding.dialogMonthPickerPickerTvMinute.value = hour[1].toInt() / 15
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_tv_no -> {
                dismiss()
            }
            R.id.dialog_tv_ok -> {
                v.setTag(
                    String.format(
                        "%02d",
                        mBinding.dialogMonthPickerPickerTvHour.value
                    ) + ":" + String.format(
                        "%02d",
                        mBinding.dialogMonthPickerPickerTvMinute.value * 15
                    )
                )
                listener.onClick(v)
                dismiss()
            }
        }
    }
}