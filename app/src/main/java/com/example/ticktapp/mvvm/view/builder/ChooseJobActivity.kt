package com.example.ticktapp.mvvm.view.builder

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
import androidx.recyclerview.widget.RecyclerView
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowChooseJobListAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityChooseListBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.builder.categories.ChoosedJobSuccessActivity
import com.example.ticktapp.mvvm.view.builder.postjob.PostNewJobActivity
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation


@Suppress("DEPRECATION")
public class ChooseJobActivity : BaseActivity(), OnClickListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var isReturn: Boolean? = false
    private var tradieId: String? = ""


    private lateinit var adapter: RowChooseJobListAdapter
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityChooseListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private var pageNumber = 1
    private val jobRecModel: ArrayList<JobRecModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_list)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
        setupView()
        if (tradieId?.length == 0)
            mViewModel.jobsList(pageNumber, true)
        else
            mViewModel.vouchJobsList(pageNumber, tradieId, true)
    }

    private fun getIntentData() {
        isReturn = intent.getBooleanExtra("isReturn", false)
        if (intent.hasExtra("tradieId")) {
            tradieId = intent.getStringExtra("tradieId")
        }
        if (isReturn == true) {
            mBinding.btnAddJob.text = getString(R.string.choose_job)
        }
    }

    private fun setupView() {
        adapter = RowChooseJobListAdapter(jobRecModel) {}
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvMilestoneList.layoutManager = layoutRecManager
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)
        mBinding.rvMilestoneList.addOnScrollListener(endlessScrollListener!!)

        mBinding.rvMilestoneList.adapter = adapter
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
        mBinding.tempJackBack.setOnClickListener { onBackPressed() }
        mBinding.btnAddJob.setOnClickListener {
            if (jobRecModel.size == 0) {
                startActivity(Intent(this, PostNewJobActivity::class.java))
            } else {
                var pos = -1;
                jobRecModel.forEachIndexed() { index, value ->
                    if (value.checked) {
                        pos = index
                    }
                }
                if (pos == -1) {
                    showToastShort(getString(R.string.please_select_job))
                } else {
                    if (isReturn == true) {
                        val intentData = Intent()
                        intentData.putExtra("data", jobRecModel.get(pos))
                        setResult(Activity.RESULT_OK, intentData)
                        finish()
                    } else {
                        mViewModel.inviteForJob(
                            intent.getStringExtra("data"),
                            jobRecModel.get(pos).jobId
                        )
                    }
                }
            }
        }

    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        pageNumber = page + 1
        if (tradieId?.length == 0)
            mViewModel.jobsList(pageNumber, false)
        else
            mViewModel.vouchJobsList(pageNumber, tradieId, false)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.JOB_LIST -> {
                showToastShort(exception.message)
            }
            ApiCodes.INVITE_FOR_JOB -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.JOB_LIST -> {
                mViewModel.mJsonResponseModelList.let {
                    if (adapter.itemCount == 0 || pageNumber
                        == 1
                    ) {
                        jobRecModel.clear()
                        jobRecModel.addAll(it)
                    } else {
                        jobRecModel.addAll(it)

                    }
                    adapter.notifyDataSetChanged()
                    hideShowNoData()
                }
                if (jobRecModel.size == 0) {
                    mBinding.btnAddJob.text = getString(R.string.post_new_job)
                }
            }
            ApiCodes.INVITE_FOR_JOB -> {
                startActivityForResult(Intent(this, ChoosedJobSuccessActivity::class.java), 1310)
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun hideShowNoData() {
        if (adapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
    }


    override fun onClick(p0: View?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

}