package com.example.ticktapp.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.databinding.DialogExpirationDateBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

/**
 * Created by Sachin on 19/6/21.
 * Prismetric Technology, Gandhinagar, Gujarat
 */
class SelectExpirationDate(
    context: Context,
    val date: String,
    val listener: View.OnClickListener
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme), View.OnClickListener {
    lateinit var mBinding: DialogExpirationDateBinding
    val cal = Calendar.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_expiration_date,
            null,
            false
        )
        setContentView(mBinding.getRoot())
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mBinding.dialogMonthPickerPickerTvHour.value = 0
        mBinding.dialogMonthPickerPickerTvMinute.value = 0
        mBinding.dialogTvNo.setOnClickListener(this)
        mBinding.dialogTvOk.setOnClickListener(this)
        try {
            if (date != null && date.length > 0) {
                mBinding.dialogMonthPickerPickerTvHour.value = Integer.parseInt(date.split("/")[0])
                mBinding.dialogMonthPickerPickerTvMinute.value =
                    2000 + Integer.parseInt(date.split("/")[1])

            } else {
                mBinding.dialogMonthPickerPickerTvHour.value = cal.get(Calendar.MONTH) + 1
                mBinding.dialogMonthPickerPickerTvMinute.value = cal.get(Calendar.YEAR)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        mBinding.dialogMonthPickerPickerTvMinute.setFormatter(object : NumberPicker.Formatter {
            override fun format(p0: Int): String {
                if (p0 == cal.get(Calendar.YEAR)) {
                    mBinding.dialogMonthPickerPickerTvHour.minValue = cal.get(Calendar.MONTH) + 1
                } else {
                    mBinding.dialogMonthPickerPickerTvHour.minValue = 1
                }
                return p0.toString();
            }
        })

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
                    ) + "/" + String.format(
                        "%02d",
                        Integer.parseInt(
                            mBinding.dialogMonthPickerPickerTvMinute.value.toString()
                                .subSequence(2, 4).toString()
                        )
                    )
                )
                listener.onClick(v)
                dismiss()
            }
        }
    }
}