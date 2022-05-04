package com.example.ticktapp.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.databinding.DialogHoursPickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class SelectMilestoneHours(
    context: Context,
    val hours: String,
    val listener: View.OnClickListener
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme), View.OnClickListener {
    lateinit var mBinding: DialogHoursPickerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_hours_picker,
            null,
            false
        )
        var dataList = ArrayList<String>()

        for (data in 0..55) {
            if (data % 5 == 0)
                dataList.add(data.toString())
        }
        setContentView(mBinding.getRoot())
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mBinding.dialogMonthPickerPickerTvHour.value = 0
        mBinding.dialogMonthPickerPickerTvMinute.value = 0
        mBinding.dialogMonthPickerPickerTvMinute.displayedValues = null
        mBinding.dialogMonthPickerPickerTvMinute.minValue = 1
        mBinding.dialogMonthPickerPickerTvMinute.maxValue = dataList.size
        mBinding.dialogMonthPickerPickerTvMinute.displayedValues = dataList.toTypedArray()
        mBinding.dialogTvNo.setOnClickListener(this)
        mBinding.dialogTvOk.setOnClickListener(this)
        if (hours.length > 0) {
            val hour = hours.split(":")
            mBinding.dialogMonthPickerPickerTvHour.value = hour[0].toInt()
            when (hour[1]) {
                "00" -> mBinding.dialogMonthPickerPickerTvMinute.value = 1
                "05" -> mBinding.dialogMonthPickerPickerTvMinute.value = 2
                "10" -> mBinding.dialogMonthPickerPickerTvMinute.value = 3
                "15" -> mBinding.dialogMonthPickerPickerTvMinute.value = 4
                "20" -> mBinding.dialogMonthPickerPickerTvMinute.value = 5
                "25" -> mBinding.dialogMonthPickerPickerTvMinute.value = 6
                "30" -> mBinding.dialogMonthPickerPickerTvMinute.value = 7
                "35" -> mBinding.dialogMonthPickerPickerTvMinute.value = 8
                "40" -> mBinding.dialogMonthPickerPickerTvMinute.value = 9
                "45" -> mBinding.dialogMonthPickerPickerTvMinute.value = 10
                "50" -> mBinding.dialogMonthPickerPickerTvMinute.value = 11
                "55" -> mBinding.dialogMonthPickerPickerTvMinute.value = 12
            }

        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_tv_no -> dismiss()
            R.id.dialog_tv_ok -> {
                v.setTag(
                    String.format(
                        "%02d",
                        mBinding.dialogMonthPickerPickerTvHour.value
                    ) + ":" + String.format(
                        "%02d",
                        (mBinding.dialogMonthPickerPickerTvMinute.displayedValues.get(mBinding.dialogMonthPickerPickerTvMinute.value - 1)).toInt()
                    )
                )
                listener.onClick(v)
                dismiss()
            }
        }
    }
}