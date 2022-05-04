package com.example.ticktapp.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowChooseJobListAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.DialogSelectJobBinding
import com.app.core.model.jobmodel.JobRecModel
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by Sachin on 19/6/21.
 * Prismetric Technology, Gandhinagar, Gujarat
 */
class SelectJobDialog(
    context: Context,
    val lists: ArrayList<JobRecModel>,
    val listener: View.OnClickListener
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme), View.OnClickListener {
    lateinit var mBinding: DialogSelectJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_select_job,
            null,
            false
        )
        setContentView(mBinding.getRoot())
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mBinding.rvJobList.layoutManager = LinearLayoutManager(context)
        val adapter = RowChooseJobListAdapter(lists) {}
        mBinding.rvJobList.adapter = adapter
        mBinding.dialogTvNo.setOnClickListener(this)
        mBinding.dialogTvOk.setOnClickListener(this)
        if (lists == null || lists.size == 0) {
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_tv_no -> {
                dismiss()

            }
            R.id.dialog_tv_ok -> {
                var pos = -1
                lists.forEachIndexed { index, it ->
                    if (it.checked) {
                        pos = index
                    }
                }
                if (pos == -1) {
                    if (context is BaseActivity) {
                        (context as BaseActivity).showToastShort(context.getString(R.string.please_select_job))
                    }
                } else {
                    v.setTag(pos)
                    listener.onClick(v)
                    dismiss()
                }
            }
        }
    }
}