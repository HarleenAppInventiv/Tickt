package com.example.ticktapp.mvvm.view.builder.milestone

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.cards.CreditCard
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneConfirmPayBinding
import com.app.core.model.jobmodel.JobMilestStonRespnse
import com.example.ticktapp.mvvm.viewmodel.CardViewModel


@Suppress("DEPRECATION")
public class MilestoneConfirmPayActivity : BaseActivity(),
    OnClickListener {
    private var card: CreditCard? = null
    private var cardId: String = ""
    private lateinit var data: JobMilestStonRespnse
    private var pos: Int = -1
    private lateinit var mBinding: ActivityMilestoneConfirmPayBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(CardViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_milestone_confirm_pay)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
    }

    private fun getIntentData() {
        if (intent.getSerializableExtra("data") is JobMilestStonRespnse) {
            data = intent.getSerializableExtra("data") as JobMilestStonRespnse
            pos = intent.getIntExtra("pos", -1)
            mBinding.ilHeader.tvTitle.text = data.jobName
        }
        mViewModel.getLastUSEDCARD()
        setData()
    }

    private fun setData() {
        if (data != null) {
            mBinding.tvMilestoneTax.text = data.milestones?.get(pos)?.taxes
            mBinding.tvPlatformFee.text = data.milestones?.get(pos)?.platformFees
            mBinding.tvMilestoneAmount.text = data.milestones?.get(pos)?.milestoneAmount
            mBinding.tvTotalAmount.text = data.milestones?.get(pos)?.total
            mBinding.tvHoursWork.text = data.milestones?.get(pos)?.hoursWorked
            mBinding.tvHoursWorkRate.text = data.milestones?.get(pos)?.hourlyRate
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }


    private fun listener() {
        mBinding.ilHeader.ivBack.setOnClickListener { onBackPressed() }
        mBinding.userIvRedirection.setOnClickListener {
            if (card != null)
                startActivityForResult(
                    Intent(this, MilestoneConfirmPaySelectActivity::class.java).putExtra(
                        "data",
                        card
                    ), 1310
                )
        }
        mBinding.btnSend.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    MilestoneConfirmPaySelectActivity::class.java
                ).putExtra("data", data).putExtra("pos", pos).putExtra("cardId", cardId), 1310
            )
        }
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.GET_LAST_USED_CARD -> {
                mBinding.llCreditCard.visibility = View.GONE
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.GET_LAST_USED_CARD -> {
                mViewModel.creditCard.let {
                    cardId = it.cardId
                    card = it
                    mBinding.llCreditCard.visibility = View.VISIBLE
                    mBinding.tvCardNumber.text = "XXXX " + it.last4
                    if (it.funding.equals("credit"))
                        mBinding.tvCardUser.text = getString(R.string.credit_card)
                    else
                        mBinding.tvCardUser.text = it.funding
                }
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            mViewModel.getLastUSEDCARD()
        }
    }
}