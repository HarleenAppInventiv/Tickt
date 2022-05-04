package com.example.ticktapp.mvvm.view.builder.milestone

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.cards.CreditCard
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAddCardBinding
import com.example.ticktapp.dialog.SelectExpirationDate
import com.example.ticktapp.mvvm.viewmodel.CardViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
public class AddCardActivity : BaseActivity(),
    OnClickListener {
    private var cards: CreditCard? = null
    private lateinit var mBinding: ActivityAddCardBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(CardViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
    }

    private fun getIntentData() {
        if (intent.hasExtra("data")) {
            cards = intent.getSerializableExtra("data") as CreditCard
            //mBinding.ivCardDelete.visibility = View.VISIBLE
            mBinding.tvAddCardDetails.text = getString(R.string.save_changes)
            setData()
        } else {
            mBinding.ivCardDelete.visibility = View.GONE
        }
    }

    private fun setData() {
        mBinding.edCardNumber.inputType = InputType.TYPE_CLASS_TEXT
        mBinding.edCardNumber.setText("XXXX XXXX XXXX " + cards?.last4)
        mBinding.edCardNumber.isEnabled = false
        mBinding.edCardNumber.setTextColor(ContextCompat.getColor(this, R.color.color_a8b4bc))
        mBinding.edCvv.setText("XXX")
        mBinding.edCvv.setTextColor(ContextCompat.getColor(this, R.color.color_a8b4bc))
        mBinding.edCvv.isEnabled = false
        mBinding.edCardName.setText(cards?.name)
        try {

            mBinding.edExpiryDate.setText(
                getMonth() + "/" + cards?.exp_year?.substring(2)
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getMonth(): String {
        try {
            if (cards?.exp_month.toString().toDouble().toInt().toString().length == 1) {
                return "0" + cards?.exp_month.toString().toDouble().toInt().toString()
            }
            return cards?.exp_month.toString().toDouble().toInt().toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
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
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.edExpiryDate.setOnClickListener {
            SelectExpirationDate(
                this,
                mBinding.edExpiryDate.text.toString(),
                object : OnClickListener {
                    override fun onClick(p0: View?) {
                        val date = p0?.getTag().toString()
                        mBinding.edExpiryDate.text = date
                    }
                }).show()
        }
        mBinding.edCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val origin: String = s.toString().replace(" ", "")
                val formatStr: String = formatStrWithSpaces(origin)
                if (!s.toString().equals(formatStr)) {
                    editTextSetContentMemorizeSelection(mBinding.edCardNumber, formatStr)
                    if (before == 0 && count == 1 && formatStr[mBinding.edCardNumber.selectionEnd - 1] == ' ') {
                        mBinding.edCardNumber.setSelection(mBinding.edCardNumber.selectionEnd + 1)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        mBinding.tvAddCardDetails.setOnClickListener {
            if (validation()) {
                if (cards != null) {
                    val params = HashMap<String, Any>()
                    if (cards!!.cardId != null && cards!!.cardId.length > 0) {
                        cards?.cardId?.let { it1 ->
                            params.put(
                                "cardId",
                                it1
                            )
                        }
                    } else if (cards!!.id != null && cards!!.id.length > 0) {
                        cards?.id?.let { it1 ->
                            params.put(
                                "cardId",
                                it1
                            )
                        }
                    }
                    params.put("name", mBinding.edCardName.text.toString().trim())
                    params.put("exp_month", mBinding.edExpiryDate.text.toString().split("/")[0])
                    params.put(
                        "exp_year",
                        "20" + mBinding.edExpiryDate.text.toString().split("/")[1]
                    )
                    mViewModel.updateCards(params)
                } else {
                    val params = HashMap<String, Any>()
                    params.put(
                        "number",
                        mBinding.edCardNumber.text.toString().trim().replace(" ", "")
                    )
                    params.put("name", mBinding.edCardName.text.toString().trim())
                    params.put("cvc", mBinding.edCvv.text.toString().trim())
                    params.put("exp_month", mBinding.edExpiryDate.text.toString().split("/")[0])
                    params.put(
                        "exp_year",
                        "20" + mBinding.edExpiryDate.text.toString().split("/")[1]
                    )
                    mViewModel.addCards(params)
                }
            }
        }
        mBinding.ivCardDelete.setOnClickListener {
            if (cards != null) {
                showAppPopupDialog(
                    getString(R.string.are_you_want_to_delete_card),
                    getString(R.string.yes),
                    getString(R.string.no),
                    getString(R.string.delete),
                    {
                        val params = HashMap<String, Any>()
                        if (cards!!.cardId != null && cards!!.cardId.length > 0) {
                            cards?.cardId?.let { it1 ->
                                params.put(
                                    "cardId",
                                    it1
                                )
                            }
                        } else if (cards!!.id != null && cards!!.id.length > 0) {
                            cards?.id?.let { it1 ->
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
            }
        }
    }

    private fun validation(): Boolean {
        if (mBinding.edCardNumber.text?.isEmpty() == true) {
            showToastShort(getString(R.string.please_enter_card_number))
            return false;
        }
        if (mBinding.edCardNumber.text?.length != 19) {
            showToastShort(getString(R.string.please_enter_valid_card_number))
            return false;
        }
        if (mBinding.edCardName.text?.isEmpty() == true) {
            showToastShort(getString(R.string.please_enter_cardholder_name))
            return false;
        }
        if (mBinding.edExpiryDate.text?.isEmpty() == true) {
            showToastShort(getString(R.string.please_enter_expiration_date))
            return false;
        }
        if (mBinding.edExpiryDate.text?.length != 5 || mBinding.edExpiryDate.text?.contains("/") == false) {
            showToastShort(getString(R.string.please_enter_valid_expiration_date))
            return false;
        }
        if (mBinding.edCvv.text?.isEmpty() == true) {
            showToastShort(getString(R.string.please_enter_cvv_cvc))
            return false;
        }
        if (mBinding.edCvv.text?.length != 3) {
            showToastShort(getString(R.string.please_valid_enter_cvv_cvc))
            return false;
        }
        return true
    }

    private fun editTextSetContentMemorizeSelection(
        editText: EditText,
        charSequence: CharSequence
    ) {
        var selectionStart = editText.selectionStart
        var selectionEnd = editText.selectionEnd
        editText.setText(charSequence.toString())
        if (selectionStart > charSequence.toString().length) {
            selectionStart = charSequence.toString().length
        }
        if (selectionStart < 0) {
            selectionStart = 0
        }
        if (selectionEnd > charSequence.toString().length) {
            selectionEnd = charSequence.toString().length
        }
        if (selectionEnd < 0) {
            selectionEnd = 0
        }
        editText.setSelection(selectionStart, selectionEnd)
    }

    private fun formatStrWithSpaces(can: CharSequence): String {
        val sb = StringBuffer()
        for (i in can.indices) {
            if (i != 0 && (i == 4 || i == 8 || i == 12 || i == 16)) {
                sb.append(' ')
            }
            sb.append(can[i])
        }
        return sb.toString()
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ADD_CARD -> {
                showToastShort(exception.message)
            }
            ApiCodes.UPDATE_CARD -> {
                showToastShort(exception.message)
            }
            ApiCodes.DELETE_CARD -> {
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.ADD_CARD -> {

                addedPaymentDetailsMoEngage() // added payment details/card mo engage
                addedPaymentDetailsMixPanel()
                mViewModel.creditCard.let {
                    val data = Intent()
                    data.putExtra("data", it)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }

            }
            ApiCodes.UPDATE_CARD -> {
                mViewModel.creditCard.let {
                    val data = Intent()
                    data.putExtra("data", it)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            }
            ApiCodes.DELETE_CARD -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

    private fun addedPaymentDetailsMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        MoEngageUtils.sendEvent(this, MoEngageConstants.MOENGAGE_EVENT_ADDED_PAYMENT_DETAILS, signUpProperty)
    }

    private fun addedPaymentDetailsMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_ADDED_PAYMENT_DETAILS,
            props
        )
    }

}