package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchPaymentScreenBinding
import com.app.core.model.jobmodel.JobModel
import com.example.ticktapp.util.DecimalDigitsInputFilter
import com.example.ticktapp.util.preventTwoClick


public class SearchAmountActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySearchPaymentScreenBinding
    private lateinit var data: JobModel
    private var isSearchType: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_payment_screen)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setupView()
        listener()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
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

    fun getIntentData() {
        data = intent.getSerializableExtra("data") as JobModel;
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setupView() {
        mBinding.tvTitle.text = data.name
        mBinding.tvDetails.text = data.trade_name
        Glide.with(mBinding.root.context).load(data.image)
            .into(mBinding.ivUserProfile)
        mBinding.edtAmount.filters = arrayOf(DecimalDigitsInputFilter(6, 2))
        mBinding.edtAmount.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (mBinding.edtAmount.text?.length!! > 0) {
                        mBinding.tvAmountError.visibility = GONE
                        startActivity(
                            Intent(
                                this@SearchAmountActivity,
                                SearchLocationActivity::class.java
                            ).putExtra("data", data)
                                .putExtra("isSearchType", isSearchType)
                                .putExtra("amount", mBinding.edtAmount.text.toString())
                        )
                    } else {
                        mBinding.tvAmountError.visibility = VISIBLE
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
                mBinding.tvAmountError.visibility = GONE
                if (p0 != null) {
                    if (p0.isNotEmpty()) {
                        mBinding.edtAmountSign.visibility = VISIBLE
                    } else {
                        mBinding.edtAmountSign.visibility = GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun listener() {
        mBinding.searchToolbarBack.setOnClickListener { onBackPressed() }
        mBinding.llTimePicker.setOnClickListener { showPopup(it) }
        mBinding.tvSkip.setOnClickListener {
            preventTwoClick(mBinding.tvSkip)
            startActivity(Intent(this, SearchLocationActivity::class.java).putExtra("data", data))
        }
        mBinding.tvContinue.setOnClickListener {
            if (mBinding.edtAmount.text?.length!! > 0) {
                if (mBinding.edtAmount.text.toString().toDouble() > 0) {
                    mBinding.tvAmountError.visibility = GONE
                    startActivity(
                        Intent(
                            this@SearchAmountActivity,
                            SearchLocationActivity::class.java
                        ).putExtra("data", data)
                            .putExtra("isSearchType", isSearchType)
                            .putExtra("amount", mBinding.edtAmount.text.toString())
                    )
                } else {
                    mBinding.tvAmountError.text = getString(R.string.valid_amount)
                    mBinding.tvAmountError.visibility = VISIBLE
                }
            } else {
                mBinding.tvAmountError.text = getString(R.string.enter_amount)
                mBinding.tvAmountError.visibility = VISIBLE
            }
        }
        mBinding.edtAmount.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (mBinding.edtAmount.text?.length!! > 0) {
                        mBinding.tvAmountError.visibility = GONE
                        startActivity(
                            Intent(
                                this@SearchAmountActivity,
                                SearchLocationActivity::class.java
                            ).putExtra("data", data)
                                .putExtra("isSearchType", isSearchType)
                                .putExtra("amount", mBinding.edtAmount.text.toString())
                        )
                    } else {
                        mBinding.tvAmountError.visibility = VISIBLE
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
                mBinding.tvAmountError.visibility = GONE
                if (p0 != null) {
                    if (p0.isNotEmpty()) {
                        mBinding.edtAmountSign.visibility = VISIBLE
                    } else {
                        mBinding.edtAmountSign.visibility = GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }
}