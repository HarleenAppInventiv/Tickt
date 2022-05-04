package com.example.ticktapp.mvvm.view.builder.profile

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.cards.CreditCard
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.CardAdapter
import com.example.ticktapp.adapters.CardOnlyAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneConfirmSelectPayBinding
import com.example.ticktapp.mvvm.view.builder.milestone.AddCardActivity
import com.example.ticktapp.mvvm.viewmodel.CardViewModel


@Suppress("DEPRECATION")
public class PaymentSelectActivity : BaseActivity(),
    OnClickListener {
    private var lastPos: Int = -1
    private var cardId: String = ""
    private lateinit var mBinding: ActivityMilestoneConfirmSelectPayBinding
    private lateinit var mAdapter: CardOnlyAdapter
    private val cards by lazy { ArrayList<CreditCard>() }
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

    private fun getIntentData() {
        mViewModel.getAllCards()
    }

    private fun setUpRecyclerView() {
        mBinding.tvTitle.text = getString(R.string.payment_details)
        mBinding.btnPay.visibility = View.GONE
        mAdapter = CardOnlyAdapter(cards, object : CardOnlyAdapter.OnCardItemClickListener {
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
        mBinding.ilHeader.ivBack.setOnClickListener { onBackPressed() }
        mBinding.tvAddAnotherCard.setOnClickListener {
            startActivityForResult(Intent(this, AddCardActivity::class.java), 1310)
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
        }
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

            ApiCodes.DELETE_CARD -> {
                if (lastPos >= 0)
                    cards.removeAt(lastPos)
                mAdapter.notifyDataSetChanged()
                if(cards.size==0){
                    mBinding.tvAddAnotherCard.text = getString(R.string.add_card)
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
            if (data != null && data.hasExtra("data")) {
                val card = data.getSerializableExtra("data") as CreditCard
                cards.add(card)
                mAdapter.notifyDataSetChanged()
            } else {
                mViewModel.getAllCards()
            }
            mBinding.tvAddAnotherCard.text = getString(R.string.add_another_card)
        }
        if (requestCode == 2610 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val card = data.getSerializableExtra("data") as CreditCard
                if (lastPos >= 0)
                    cards.set(lastPos, card)
                mAdapter.notifyDataSetChanged()
            } else {
                mViewModel.getAllCards()
            }
        }
    }
}