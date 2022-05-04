package com.example.ticktapp.mvvm.view.builder.milestone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.app.core.util.StripeConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmSetupIntentParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.view.BecsDebitWidget
import kotlinx.android.synthetic.main.activity_stripe_becsactivity.*
import kotlinx.android.synthetic.main.layout_right_icon_toolbar.*

class StripeBECSActivity : BaseActivity() {

    private val stripe: Stripe by lazy {
        Stripe(this, StripeConstants.PUBLISHABLE_KEY)
    }
    var TAG: String = "StripeBecsActivity"
    var paymentLauncherObject: PaymentLauncher? = null
    private lateinit var paymentIntentClientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_becsactivity)

        getIntentData()
        listeners()

        /*val paymentLauncher = PaymentLauncher.Companion.create(
            this,
            StripeConstants.PUBLISHABLE_KEY,
            PaymentConfiguration.getInstance(applicationContext).stripeAccountId,
            ::onPaymentResult
        )
*/
        paymentLauncherObject = paymentLauncher

        tv_title.text = getString(R.string.payment)

        becs_debit_widget.validParamsCallback =
            object : BecsDebitWidget.ValidParamsCallback {
                override fun onInputChanged(isValid: Boolean) {
                    // enable payButton if the customer's input is valid
                    pay_button.isEnabled = isValid
                }
            }
    }

    private fun listeners() {
        pay_button.setOnClickListener { onPayClicked(paymentIntentClientSecret) }
        iv_back.setOnClickListener { onBackPressed() }
    }

    private fun getIntentData() {
        paymentIntentClientSecret = intent!!.extras!!.getString("clientSecret", "")
        Log.i("Stripe_payment", "createPaymentIntent: $paymentIntentClientSecret")
    }

    private val paymentLauncher: PaymentLauncher by lazy {
        PaymentLauncher.Companion.create(
            this,
            StripeConstants.PUBLISHABLE_KEY,
            PaymentConfiguration.getInstance(applicationContext).stripeAccountId,
            ::onPaymentResult
        )
    }

    private fun onPayClicked(paymentIntentClientSecret: String) {
        becs_debit_widget.params?.let { params ->
            paymentLauncherObject!!.confirm(
                ConfirmSetupIntentParams.create(
                    paymentMethodCreateParams = params,
                    clientSecret = paymentIntentClientSecret
                )
            )
        }
    }

    private fun onPaymentResult(paymentResult: PaymentResult) {
        when (paymentResult) {
            is PaymentResult.Completed -> {
                // PaymentIntent confirmation succeeded
                showToastShort("Completed")
                var intent: Intent = Intent()
                intent.putExtra("status", "Completed")
                setResult(2000, intent)
                finish()
            }
            is PaymentResult.Canceled -> {
                // PaymentIntent confirmation canceled
                showToastShort("Canceled")
                var intent: Intent = Intent()
                intent.putExtra("status", "Canceled")
                setResult(2000, intent)
                finish()
            }

            is PaymentResult.Failed -> {
                // PaymentIntent confirmation failed see here for message:
                // ((PaymentResult.Failed) paymentResult).getThrowable().getMessage();
                Log.i(TAG, "onPaymentResult: ${paymentResult.throwable.message}")
                showToastShort("Failed")
                var intent: Intent = Intent()
                intent.putExtra("status", "Failed")
                setResult(2000, intent)
                finish()
            }
        }
    }
}