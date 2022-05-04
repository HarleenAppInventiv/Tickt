package com.example.ticktapp.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.interfaces.DialogCallback
import kotlinx.android.synthetic.main.dialog_cancel_rejection_reason.view.*

object DialogUtils {


    fun setCustomAlert(
            mContext: BaseActivity,
            mInterface: DialogCallback

    ): Dialog {
        val dialogView = Dialog(mContext)
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
                DataBindingUtil.inflate<ViewDataBinding>(
                        LayoutInflater.from(mContext),
                        R.layout.dialog_cancel_rejection_reason,
                        null,
                        false
                )


        dialogView.setContentView(binding.root)
        dialogView.setCancelable(false)

        dialogView.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val tvCancelAccept = dialogView.findViewById<TextView>(R.id.tvCancelAccept)
        val tvCancelReject = dialogView.findViewById<TextView>(R.id.tvCancelReject)

        tvCancelAccept.setOnClickListener {
            if(binding.root.job_desc_ed.text.toString().trim().isEmpty())
            {
                mContext.showToastShort("please write decline reason")
                return@setOnClickListener
            }
            dialogView.dismiss()
            mInterface.onPositiveClick(binding.root.job_desc_ed.text.toString())

        }
        tvCancelReject.setOnClickListener {
            dialogView.dismiss()
        }
        dialogView.show()


        return dialogView

    }
}