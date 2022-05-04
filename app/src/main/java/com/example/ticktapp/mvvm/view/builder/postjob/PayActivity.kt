package com.example.ticktapp.mvvm.view.builder.postjob

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityPayBinding
import com.example.ticktapp.util.DecimalDigitsInputFilter
import com.example.ticktapp.util.getPojoData

class PayActivity : BaseActivity() {
    private lateinit var mBinding: ActivityPayBinding
    private var isSearchType: Int = 1
    private var isJobType: Int = 1
    private var data: JobRecModelRepublish? = null
    private var isDisable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pay)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        getIntentData()
    }

    private fun getIntentData() {
        if (PreferenceManager.getInt(PreferenceManager.NEW_JOB_PREF.JOB_BUDGET_TYPE,-1) != -1) {
            isSearchType = PreferenceManager.getInt(PreferenceManager.NEW_JOB_PREF.JOB_BUDGET_TYPE)
            isJobType = PreferenceManager.getInt(PreferenceManager.NEW_JOB_PREF.JOB_PAY_TYPE,1)?:1
//            mBinding.edtAmountSign.visibility = View.VISIBLE

            if (isJobType==2) {
                mBinding.llBudget.alpha = 0.5f
                mBinding.llQuote.alpha = 1f
                mBinding.ivQuote.setImageResource(R.drawable.radio_check)
                mBinding.ivBudget.setImageResource(R.drawable.radio_drawable)

//                isDisable = true
            } else {
                mBinding.llQuote.alpha = 0.5f
                mBinding.llBudget.alpha = 1f
                mBinding.edtAmount.setText(PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_AMOUNT))
                when (isSearchType) {
                    1 -> {
                        mBinding.tvSelectedText.text = getString(R.string.per_hour)
                    } else -> {

                    mBinding.tvSelectedText.text = getString(R.string.fixed_price)
                }
                }
            }

        }else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA).isNullOrEmpty()) {
            data = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .getPojoData(JobRecModelRepublish::class.java)
            isJobType = if (data?.quoteJob == true) 2 else 1
            isSearchType = if (data?.pay_type.equals("Per hour")) 1 else 2
//            mBinding.edtAmountSign.visibility = View.VISIBLE

            if (isJobType == 2) {
                mBinding.ivQuote.setImageResource(R.drawable.radio_check)
                mBinding.ivBudget.setImageResource(R.drawable.radio_drawable)
                mBinding.llBudget.alpha = 0.5f
                mBinding.llQuote.alpha = 1f
//                isDisable = true
            } else {
                mBinding.edtAmount.setText(data?.amount)
                mBinding.llQuote.alpha = 0.5f
                mBinding.llBudget.alpha = 1f
                when (isSearchType) {
                    1 -> {
                        mBinding.tvSelectedText.text = getString(R.string.per_hour)
                    } else -> {

                    mBinding.tvSelectedText.text = getString(R.string.fixed_price)
                }
                }
            }

        }else if (intent.hasExtra("data")) {
            data = intent.getSerializableExtra("data") as JobRecModelRepublish
//            mBinding.edtAmountSign.visibility = View.VISIBLE
            if (data?.quoteJob != null && data?.quoteJob == true) {
                mBinding.ivQuote.setImageResource(R.drawable.radio_check)
                mBinding.ivBudget.setImageResource(R.drawable.radio_drawable)
                mBinding.llBudget.alpha = 0.5f
                mBinding.llQuote.alpha = 1f
                isJobType = 2
                isDisable = true
            } else {
                isJobType=1
                mBinding.edtAmount.setText(data?.amount)
                mBinding.llQuote.alpha = 0.5f
                mBinding.llBudget.alpha = 1f
                if (data?.pay_type.equals("Per hour")) {
                    isSearchType = 1
                    mBinding.tvSelectedText.text = getString(R.string.per_hour)
                } else {
                    isSearchType = 2
                    mBinding.tvSelectedText.text = getString(R.string.fixed_price)
                }
            }
        }

    }

        private fun setLightStatusBar(view: View) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                view.systemUiVisibility = flags
                window.statusBarColor = Color.WHITE
            }
        }

        private fun setStatusBarColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.WHITE
            }
        }

        private fun isValid(): Boolean {
            if (isJobType==2)
            {
                return true
            }
            else if (isJobType == 1) {
                if (mBinding.edtAmount.text.toString().trim().isNullOrEmpty()) {
                    showToastShort(getString(R.string.please_enter_amount))
                    return false
                }
                else if (mBinding.edtAmount.text.toString().toDouble() == 0.0) {
                    showToastShort(getString(R.string.valid_amount))
                    return false
                }
                else {
                    return true
                }
            }else{
                showToastShort("Please select jobType")

                return false}
        }

        private fun listener() {
            mBinding.payBack.setOnClickListener { onBackPressed() }
            mBinding.llTimePicker.setOnClickListener { showPopup(it) }
            mBinding.ivBudget.setOnClickListener {
//                if (!isDisable) {
                    mBinding.ivQuote.setImageResource(R.drawable.radio_drawable)
                    mBinding.ivBudget.setImageResource(R.drawable.radio_check)
                mBinding.llQuote.alpha = 0.5f
                mBinding.llBudget.alpha = 1f
                    isJobType = 1
//                }
            }
            mBinding.ivQuote.setOnClickListener {
                mBinding.ivQuote.setImageResource(R.drawable.radio_check)
                mBinding.ivBudget.setImageResource(R.drawable.radio_drawable)
                mBinding.llBudget.alpha = 0.5f
                mBinding.llQuote.alpha = 1f
                mBinding.edtAmount.text?.clear()
                isJobType = 2
            }
            mBinding.jobDescBtn.setOnClickListener {
                if (isValid()) {
                  goToNextActivity()
                  /*  if (data != null) {
                        startActivity(
                            Intent(this, DateActivity::class.java)
                                .putExtra(
                                    "jobName",
                                    intent.getStringExtra("jobName")
                                ).putExtra(
                                    "categories",
                                    intent.getSerializableExtra("categories")
                                ).putExtra(
                                    "job_type",
                                    intent.getSerializableExtra("job_type")
                                ).putExtra(
                                    "specialization",
                                    intent.getSerializableExtra("specialization")
                                ).putExtra(
                                    "lat",
                                    intent.getStringExtra("lat")
                                ).putExtra(
                                    "lng",
                                    intent.getStringExtra("lng")
                                )
                                .putExtra("location_name", intent.getStringExtra("location_name"))
                                .putExtra(
                                    "job_description",
                                    intent.getStringExtra("job_description")
                                )
                                .putExtra("isSearchType", isSearchType)
                                .putExtra("isJobType", isJobType)
                                .putExtra("amount", mBinding.edtAmount.text.toString())
                                .putExtra("data", data)
                        )
                    } else {
                        startActivity(
                            Intent(this, DateActivity::class.java)
                                .putExtra(
                                    "jobName",
                                    intent.getStringExtra("jobName")
                                ).putExtra(
                                    "categories",
                                    intent.getSerializableExtra("categories")
                                ).putExtra(
                                    "job_type",
                                    intent.getSerializableExtra("job_type")
                                ).putExtra(
                                    "specialization",
                                    intent.getSerializableExtra("specialization")
                                ).putExtra(
                                    "lat",
                                    intent.getStringExtra("lat")
                                ).putExtra(
                                    "lng",
                                    intent.getStringExtra("lng")
                                )
                                .putExtra("location_name", intent.getStringExtra("location_name"))
                                .putExtra(
                                    "job_description",
                                    intent.getStringExtra("job_description")
                                )
                                .putExtra("isSearchType", isSearchType)
                                .putExtra("isJobType", isJobType)
                                .putExtra("amount", mBinding.edtAmount.text.toString())
                        )
                    }
*/
                }
            }
            mBinding.edtAmount.filters = arrayOf(DecimalDigitsInputFilter(6, 2))
            mBinding.edtAmount.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        if (isValid()) {
                            goToNextActivity()

                            /*    startActivity(
                                    Intent(this@PayActivity, DateActivity::class.java)
                                        .putExtra(
                                            "jobName",
                                            intent.getStringExtra("jobName")
                                        ).putExtra(
                                            "categories",
                                            intent.getSerializableExtra("categories")
                                        ).putExtra(
                                            "job_type",
                                            intent.getSerializableExtra("job_type")
                                        ).putExtra(
                                            "specialization",
                                            intent.getSerializableExtra("specialization")
                                        ).putExtra(
                                            "lat",
                                            intent.getStringExtra("lat")
                                        ).putExtra(
                                            "lng",
                                            intent.getStringExtra("lng")
                                        )
                                        .putExtra(
                                            "location_name",
                                            intent.getStringExtra("location_name")
                                        )
                                        .putExtra(
                                            "job_description",
                                            intent.getStringExtra("job_description")
                                        )
                                        .putExtra("isSearchType", isSearchType)
                                        .putExtra("isJobType", isJobType)
                                        .putExtra("amount", mBinding.edtAmount.text.toString())
                                )*/
                        }
                        return true
                    }
                    return false
                }
            })
            mBinding.edtAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0 != null) {
                        if (p0.trim().isNotEmpty() ) {
                            mBinding.edtAmountSign.visibility = View.VISIBLE
                            mBinding.llBudget.alpha=1f
                            mBinding.llQuote.alpha=0.5f
                            mBinding.ivBudget.setImageResource(R.drawable.radio_check)
                            mBinding.ivQuote.setImageResource(R.drawable.radio_drawable)
                        } else {
                            mBinding.edtAmountSign.visibility = View.GONE
                        }
                    }else{
                        mBinding.edtAmountSign.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

        }

    private fun goToNextActivity() {
        PreferenceManager.putInt(PreferenceManager.NEW_JOB_PREF.JOB_PAY_TYPE, isJobType)
        PreferenceManager.putInt(
            PreferenceManager.NEW_JOB_PREF.JOB_BUDGET_TYPE,
            isSearchType
        )
        PreferenceManager.putString(
            PreferenceManager.NEW_JOB_PREF.JOB_AMOUNT,
            mBinding.edtAmount.text.toString()
        )
        startActivity(Intent(this@PayActivity, DateActivity::class.java).apply {
            if (this@PayActivity.data != null) {
                putExtra("data", this@PayActivity.data)
            }
        })
    }

    private fun showPopup(view: View) {
            mBinding.ivDropDown.animate().rotation(180f).start();
            val popup = PopupMenu(this, view)
            popup.inflate(R.menu.time_hour_menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.tv_per_hours -> {
                        mBinding.tvSelectedText.text = getString(R.string.per_hour)
                        isSearchType = 1
                    }
                    R.id.tv_fixed_price -> {
                        mBinding.tvSelectedText.text = getString(R.string.fixed_price)
                        isSearchType = 2
                    }
                }

                true
            })
            popup.setOnDismissListener {
                mBinding.ivDropDown.animate().rotation(0f).start();
            }
            popup.show()
        }


    }