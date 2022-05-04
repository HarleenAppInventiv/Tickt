package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.tradie.VouchesData
import com.example.ticktapp.R
import com.example.ticktapp.adapters.VounchesFullAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieListBinding

class VounchListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {
    private var isMyVouch: Boolean = false
    private lateinit var mAdapter: VounchesFullAdapter
    private lateinit var mBinding: ActivityTradieListBinding
    private lateinit var title: String
    private lateinit var list: ArrayList<VouchesData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_list)
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setData()
        initRecyclerView()
    }

    private fun setData() {
        mBinding.tvTitle.text = title
        mBinding.llMainBg.setBackgroundColor(Color.WHITE)
        if (!isMyVouch)
            mBinding.tvVouches.visibility = View.VISIBLE
    }


    private fun getIntentData() {
        list = intent.getSerializableExtra("data") as ArrayList<VouchesData>
        title = intent.getStringExtra("title").toString()
        if (intent.hasExtra("isMyVouch")) {
            isMyVouch = intent.getBooleanExtra("isMyVouch", false)
        }
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.tvVouches.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    AddVoucherBuilderActivity::class.java
                ).putExtra("id", intent.getStringExtra("id")), 1310
            )
        }
    }

    override fun onBackPressed() {
        val data = Intent()
        data.putExtra("data", list)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun initRecyclerView() {
        mAdapter = VounchesFullAdapter(list)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvTradie.layoutManager = layoutRecManager
        mBinding.rvTradie.adapter = mAdapter

    }


    private fun hideShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
    }


    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    override fun onClick(p0: View?) {

    }

    override fun onRefresh() {
        mBinding.srLayout.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val vouchesData = data.getSerializableExtra("data") as VouchesData
                list.add(vouchesData)
                mAdapter.notifyDataSetChanged()
                hideShowNoData()
                mBinding.tvTitle.text = (list?.size).toString() + " vouche(s)"
            }
        }
    }

}
