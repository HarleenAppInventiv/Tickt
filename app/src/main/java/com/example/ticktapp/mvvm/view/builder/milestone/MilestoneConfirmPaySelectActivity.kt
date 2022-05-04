package com.example.ticktapp.mvvm.view.builder.milestone

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.cards.CreditCard
import com.example.ticktapp.R
import com.example.ticktapp.adapters.CardAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneConfirmSelectPayBinding
import com.app.core.model.jobmodel.JobMilestStonRespnse
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.mvvm.viewmodel.CardViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.stripe.android.model.PaymentIntent

import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmSetupIntentParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult


@Suppress("DEPRECATION")
public class MilestoneConfirmPaySelectActivity : BaseActivity(),
    OnClickListener {
    private var lastPos: Int = -1
    private var cardId: String = ""
    private lateinit var data: JobMilestStonRespnse
    private var pos: Int = -1
    private lateinit var mBinding: ActivityMilestoneConfirmSelectPayBinding
    private lateinit var mAdapter: CardAdapter
    private val cards by lazy { ArrayList<CreditCard>() }
    private var isRefresh: Boolean = false
    var category: String? = null

    var builderId: String = ""
    var amount: String = ""
    var tradieId: String = ""
    var jobId: String = ""
    var milestoneId: String = ""

    private lateinit var paymentIntentClientSecret: String
    val TAG = "StripeTag"
    private val mViewModel by lazy { ViewModelProvider(this).get(CardViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_milestone_confirm_select_pay)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
        setUpRecyclerView()
    }


    private fun saveTransactionBackend(status: String) {
        val params = HashMap<String, Any>()
        if (amount.contains("$")) {
            amount = amount.replace("$", "")
        }
        params.put("amount", amount)
        params.put("tradieId", tradieId)
        params.put("builderId", builderId)
        params.put("jobId", jobId)
        params.put("milestoneId", milestoneId)
        params.put("status", status)
        mViewModel.saveBankTransactionStripe(params)
    }

    private fun getIntentData() {
        if (intent.getSerializableExtra("data") is JobMilestStonRespnse) {
            data = intent.getSerializableExtra("data") as JobMilestStonRespnse
            pos = intent.getIntExtra("pos", -1)
            cardId = intent.getStringExtra("cardId").toString()
            mBinding.ilHeader.tvTitle.text = data.jobName

            amount = data.milestones!![pos].milestoneAmount!!
            tradieId = data.tradieId.toString()
            builderId = PreferenceManager.getString(PreferenceManager.USER_ID).toString()
            jobId = data.jobId!!
            milestoneId = data.milestones!![pos].milestoneId!!

            Log.i("dataMileStonePay", "getIntentData: amount $amount ")
            Log.i("dataMileStonePay", "getIntentData: tradieId $tradieId ")
            Log.i("dataMileStonePay", "getIntentData: builderId $builderId ")
            Log.i("dataMileStonePay", "getIntentData: jobId $jobId ")
            Log.i("dataMileStonePay", "getIntentData: milestoneId $milestoneId ")
        }
        mViewModel.getAllCards()
    }

    private fun setUpRecyclerView() {
        mAdapter = CardAdapter(cards, object : CardAdapter.OnCardItemClickListener {
            override fun onCardItemClick(position: Int, type: Int) {
                lastPos = position
                if (type == 1) {
                    showAppPopupDialog(
                        getString(R.string.are_you_want_to_delete_card),
                        getString(R.string.yes),
                        getString(R.string.no),
                        getString(R.string.delete),
                        {
                            val params = HashMap<String, Any>()
                            if (cards.get(position).cardId != null && cards.get(position).cardId.length > 0) {
                                cards.get(position).cardId.let { it1 ->
                                    params.put(
                                        "cardId",
                                        it1
                                    )
                                }
                            } else if (cards.get(position).id != null && cards.get(position).id.length > 0) {
                                cards.get(position).id.let { it1 ->
                                    params.put(
                                        "cardId",
                                        it1
                                    )
                                }
                            }
                            mViewModel.deleteCards(params)

                        },
                        {
                        },
                        true
                    )
                } else if (type == 2) {
                    startActivityForResult(
                        Intent(
                            mBinding.rvCards.context,
                            AddCardActivity::class.java
                        ).putExtra("data", cards.get(position)), 2610
                    )
                }
            }
        })
        mBinding.rvCards.layoutManager = LinearLayoutManager(this)
        mBinding.rvCards.adapter = mAdapter
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
        mBinding.tvPayByBank.setOnClickListener { createStripeClientSecretKey() }
        mBinding.ilHeader.ivBack.setOnClickListener { onBackPressed() }
        mBinding.tvAddAnotherCard.setOnClickListener {
            startActivityForResult(Intent(this, AddCardActivity::class.java), 1310)
        }
        mBinding.btnPay.setOnClickListener {
            val params = HashMap<String, Any>()
            data.jobId?.let { it1 ->
                params.put(
                    "jobId",
                    it1
                )
            }
            data.tradie?.tradieId?.let { it1 -> params.put("tradieId", it1) }
            data.milestones?.get(pos)?.total?.let { it1 ->
                params.put(
                    "amount",
                    it1.replace("$", "").toDouble()
                )
            }
            data.milestones?.get(pos)?.milestoneAmount?.let { it1 ->
                params.put(
                    "milestoneAmount",
                    it1.replace("$", "").toDouble()
                )
            }
            data.milestones?.get(pos)?.milestoneId?.let { it1 -> params.put("milestoneId", it1) }
            mAdapter.cardsList.forEach {
                if (it.checked) {
                    params.put("paymentMethodId", it.cardId)

                }
            }
            params.put("status", 1)
            if (params.containsKey("paymentMethodId")) {
                try {
                    category = data!!.categories?.get(0)?.trade_name ?: ""
                    Log.i("categorycategory", "getIntentData: $category")
                } catch (e: Exception) {
                    category = ""
                }
                madePaymentMoEngage(category!!)  // Made Payment Mo Engage
                madePaymentMixPanel(category!!)  // Made Payment Mo Engage
                mViewModel.pay(params)
            } else {
                showToastShort(getString(R.string.please_select_card))
            }
        }
    }

    private fun createStripeClientSecretKey() {
        val params = HashMap<String, Any>()
        if (amount.contains("$")) {
            amount = amount.replace("$", "")
        }
        params.put("amount", amount)
        params.put("tradieId", tradieId)
        params.put("builderId", builderId)
        params.put("jobId", jobId)
        params.put("milestoneId", milestoneId)
        mViewModel.createClientSecretKey(params)
    }

    override fun onBackPressed() {
        if (isRefresh) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.CARD_LIST -> {
                cards.clear()
                mAdapter.notifyDataSetChanged()
                showToastShort(exception.message)
                mBinding.tvAddAnotherCard.text = getString(R.string.add_card)
            }
            ApiCodes.DELETE_CARD -> {
                showToastShort(exception.message)
            }
            ApiCodes.PAY -> {
                paymentMoEngage(false)
                paymentMixPanel(false)
                startActivity(
                    Intent(this, PaymentSuccessActivity::class.java).putExtra(
                        IntentConstants.IS_JOB_COMPLETED,
                        false
                    ).putExtra(
                        IntentConstants.IS_LAST_JOB,
                        false
                    )
                )
            }

        }
    }

    private fun madePaymentMoEngage(category: String) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_MADE_PAYMENT,
            signUpProperty
        )
    }

    private fun madePaymentMixPanel(category: String) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_MADE_PAYMENT,
            props
        )
    }

    private fun paymentMoEngage(isSuucess: Boolean) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        if (isSuucess)
            MoEngageUtils.sendEvent(
                this,
                MoEngageConstants.MOENGAGE_EVENT_PAYMENT_SUCCESS,
                signUpProperty
            )
        else
            MoEngageUtils.sendEvent(
                this,
                MoEngageConstants.MOENGAGE_EVENT_PAYMENT_FAILURE,
                signUpProperty
            )
    }

    private fun paymentMixPanel(isSuucess: Boolean) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        if (isSuucess)
            mixpanel.track(
                MoEngageConstants.MOENGAGE_EVENT_PAYMENT_SUCCESS,
                props
            )
        else
            mixpanel.track(
                MoEngageConstants.MOENGAGE_EVENT_PAYMENT_FAILURE,
                props
            )
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CARD_LIST -> {
                mViewModel.creditCards.let {
                    cards.clear()
                    cards.addAll(it)
                    cards.forEach { if (it.cardId == cardId) it.checked = true }
                    mAdapter.notifyDataSetChanged()
                    if (cards.size == 0) {
                        mBinding.tvAddAnotherCard.text = getString(R.string.add_card)
                    } else {
                        mBinding.tvAddAnotherCard.text = getString(R.string.add_another_card)
                    }
                }
            }
            ApiCodes.PAY -> {
                paymentMoEngage(true)
                paymentMixPanel(true)
                startActivity(
                    Intent(this, PaymentSuccessActivity::class.java).putExtra(
                        IntentConstants.IS_JOB_COMPLETED,
                        true
                    ).putExtra(
                        IntentConstants.IS_LAST_JOB,
                        (data.milestones?.size?.minus(1)) == pos
                    )
                )
            }
            ApiCodes.DELETE_CARD -> {
                if (lastPos >= 0)
                    cards.removeAt(lastPos)
                mAdapter.notifyDataSetChanged()
            }
            ApiCodes.CREATE_CLIENT_SECRET -> {
                mViewModel.clientSecretResponse.clientSecret?.let {

                    paymentIntentClientSecret = it
                    Log.i(TAG, "onResponseSuccess: $paymentIntentClientSecret")
                    startActivityForResult(
                        Intent(
                            this,
                            StripeBECSActivity::class.java
                        ).putExtra("clientSecret", paymentIntentClientSecret), 2000
                    )
                }
            }
            ApiCodes.SAVE_STRIPE_BANK_TRANSACTION -> {
                startActivity(
                    Intent(this, PaymentSuccessActivity::class.java).putExtra(
                        IntentConstants.IS_JOB_COMPLETED,
                        true
                    ).putExtra(
                        IntentConstants.IS_LAST_JOB,
                        (data.milestones?.size?.minus(1)) == pos
                    )
                )
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isRefresh = true
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val card = data.getSerializableExtra("data") as CreditCard
                cards.add(card)
                mAdapter.notifyDataSetChanged()
            } else {
                mViewModel.getAllCards()
            }
            mBinding.tvAddAnotherCard.text = getString(R.string.add_another_card)
        } else if (requestCode == 2610 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val card = data.getSerializableExtra("data") as CreditCard
                if (lastPos >= 0)
                    cards.set(lastPos, card)
                mAdapter.notifyDataSetChanged()
            } else {
                mViewModel.getAllCards()
            }
        } else if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("status")) {
                var status = data.extras!!.getString("status")
                if (status == "Completed") {
                    saveTransactionBackend("Completed")
                }
            }
        }
    }
}