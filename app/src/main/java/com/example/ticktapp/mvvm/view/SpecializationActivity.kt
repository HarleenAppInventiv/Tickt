package com.example.ticktapp.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.model.tradesmodel.Qualification
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.TradeData
import com.app.core.preferences.PreferenceManager
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.SpecializationAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySpecializationBinding
import com.example.ticktapp.mvvm.viewmodel.SpecializationViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class SpecializationActivity : BaseActivity(), View.OnClickListener,
    SpecializationAdapter.SpecListAdapterListener {
    private lateinit var mBinding: ActivitySpecializationBinding
    private var tadeList: ArrayList<String>? = null
    private var specList: ArrayList<Specialisation>? = null
    private var qualList: ArrayList<Qualification>? = null
    private val selectedList by lazy { ArrayList<String>() }
    private var email: String? = null
    private var name: String? = null
    private var phoneno: String? = null
    private var password: String? = null
    private var selectedPosition = 0
    private var tradeData: ArrayList<TradeData> = ArrayList()
    private var specializatinData: ArrayList<SpecialisationData> = ArrayList()
    private var specializatinDataSelected: ArrayList<SpecialisationData> = ArrayList()
    private var isForEdit: Boolean = false

    private val mViewModel by lazy { ViewModelProvider(this).get(SpecializationViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_specialization)
        mBinding.model = mViewModel
        getIntentData()
        initView()
        if(!isForEdit)
        setProgressDots()
        setObservers()
        setListeners()
        initRecyclerView()
    }


    private fun getIntentData() {
        isForEdit = intent.getBooleanExtra("isForEdit", false)
        if (!isForEdit) {
            email = intent.getStringExtra(IntentConstants.EMAIL)
            name = intent.getStringExtra(IntentConstants.FIRST_NAME)
            phoneno = intent.getStringExtra(IntentConstants.MOBILE_NUMBER)
            password = intent.getStringExtra(IntentConstants.PASSWORD)
            tadeList = intent.getStringArrayListExtra(IntentConstants.TRADE_LIST)
            specList = intent.getParcelableArrayListExtra<Specialisation>(IntentConstants.SPEC_LIST)
            qualList = intent.getParcelableArrayListExtra<Qualification>(IntentConstants.QUAL_LIST)
        }
        else {
            tradeData = intent.getSerializableExtra("trade") as ArrayList<TradeData>
            specializatinData =
                intent.getSerializableExtra("specialization") as ArrayList<SpecialisationData>
            tadeList = intent.getStringArrayListExtra(IntentConstants.TRADE_LIST)
            specList = intent.getParcelableArrayListExtra<Specialisation>(IntentConstants.SPEC_LIST)
            specList?.let {
                for (item in specializatinData) {
                    for (data in specList!!) {
                        if (item.specializationId.equals(data.id)) {
                            data.isSelected = true
                            break
                        }
                    }
                }
            }


        }
    }


    fun setProgressDots() {
        mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
        mBinding.rlToolbar.v1.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v2.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v3.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v4.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v5.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v6.background =
            ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
        mBinding.rlToolbar.v7.background =
            ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)

        if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 1) {
            mBinding.rlToolbar.v8.visibility = View.VISIBLE
            mBinding.rlToolbar.v8.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
        }
    }


    private fun initRecyclerView() {

        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }

        mBinding.rvSpecialization.apply {
            layoutManager = flexboxLayoutManager
            adapter = specList?.let { SpecializationAdapter(it, this@SpecializationActivity) }!!
        }

        if (specList != null && specList!!.size == 0)
            mBinding.tvNoData.visibility = View.VISIBLE
        else
            mBinding.tvNoData.visibility = View.GONE

    }


    private fun setListeners() {
        mBinding.tvYellowBtn.setOnClickListener(this)
        iv_back.setOnClickListener(this)
    }

    private fun initView() {
        mBinding.rlToolbar.tvTitle.setText(getString(R.string.and_specialization))
        mBinding.tvYellowBtn.setText(getString(R.string.next))
    }

    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
        })
    }


    override fun onClick(v: View?) {
        when (v) {
            iv_back -> {
                onBackPressed()
            }
            mBinding.tvYellowBtn -> {
                selectedList.clear()
                specializatinDataSelected.clear()
                specList?.forEachIndexed { index, element ->
                    if (specList?.get(index)?.isSelected == true) {
                        val data=SpecialisationData()
                        data.specializationId=specList!!.get(index).id
                        data.specializationName=specList!!.get(index).name
                        selectedPosition = index
                        selectedList.add(specList!!.get(index).id.toString())
                        specializatinDataSelected.add(data)
                    }
                }
                if (selectedList.size > 0) {
                   if(!isForEdit) {
                       if (PreferenceManager.getString(PreferenceManager.USER_TYPE)
                               ?.toInt() == 1
                       ) {
                           startActivity(Intent(this, AddDocumentActivity::class.java).apply {
                               putExtra(IntentConstants.MOBILE_NUMBER, phoneno)
                               putExtra(IntentConstants.FIRST_NAME, name)
                               putExtra(IntentConstants.EMAIL, email)
                               putExtra(IntentConstants.PASSWORD, password)
                               putStringArrayListExtra(IntentConstants.TRADE_LIST, tadeList)
                               putStringArrayListExtra(IntentConstants.SPEC_LIST, selectedList)
                               putParcelableArrayListExtra(IntentConstants.QUAL_LIST, qualList)

                           })
                       } else {
                           startActivity(Intent(this, ABNActivity::class.java).apply {
                               val onBoardingData = OnBoardingData(
                                   firstName = name,
                                   email = email,
                                   mobileNumber = phoneno,
                                   password = password,
                                   trade = tadeList,
                                   specialization = selectedList
                               )
                               putExtra(IntentConstants.DATA, onBoardingData)
                           })
                       }
                   }
                    else
                   {
                       val intent=Intent()
                       intent. putExtra(IntentConstants.TRADE_LIST, tradeData)
                       intent. putExtra(IntentConstants.SPEC_LIST, specializatinDataSelected)
                       setResult(RESULT_OK,intent)
                       finish()
                   }
                } else
                    showToastShort(getString(R.string.please_select_specialisation))
            }
        }
    }


    override fun onSpecCLick(position: Int) {
    }

}