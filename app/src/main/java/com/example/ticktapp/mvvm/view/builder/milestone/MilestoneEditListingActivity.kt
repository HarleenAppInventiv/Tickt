package com.example.ticktapp.mvvm.view.builder.milestone

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MilestoneStatus
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowEditMilestoneAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityEditMilestoneListBinding
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.model.MilestoneEditRequestData
import com.example.ticktapp.model.MilestoneRequestData
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.jobmodel.MilestoneList
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.mvvm.view.builder.postjob.AddMilestoneActivity
import com.example.ticktapp.mvvm.viewmodel.MilestoneListViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MilestoneEditListingActivity : BaseActivity(),
    View.OnClickListener {
    private var isNotShow: Boolean = false
    private var isAlertShow: Boolean = false
    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityEditMilestoneListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MilestoneListViewModel::class.java) }
    private lateinit var mAdapter: RowEditMilestoneAdapter
    private val list by lazy { ArrayList<MilestoneList>() }
    private var dataRecModel: JobDashboardModel? = null
    private val deletedList by lazy { ArrayList<MilestoneList>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_milestone_list)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
    }

    var isjobRec: Boolean = true
    private fun getIntentData() {
        if (intent.hasExtra("isNotShow")) {
            isNotShow = intent.getBooleanExtra("isNotShow", false)
        }
        if (intent.getSerializableExtra("data") is JobRecModel) {
            data = intent.getSerializableExtra("data") as JobRecModel
            isjobRec = true
            data.jobId?.let { mViewModel.getJobMilestoneList(it) }
        } else {
            isjobRec = false
            dataRecModel = intent.getSerializableExtra("data") as JobDashboardModel
            dataRecModel!!.jobId?.let { mViewModel.getJobMilestoneList(it) }
        }
    }

    private fun setUpListeners() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.jobAddMilestone.setOnClickListener {
            val newData = ArrayList<MilestoneData>()
            list.forEach {
                val milestone = it.milestoneName?.let { it1 ->
                    MilestoneData(
                        it1, it.isPhotoevidence, false,
                        it.fromDate.toString(),
                        it.toDate.toString(), it.recommendedHours.toString()
                    )
                }
                milestone?.let { it1 -> newData.add(it1) }
            }
            startActivityForResult(
                Intent(mBinding.rvMilestoneList.context, AddMilestoneActivity::class.java)
                    .putExtra("allData", newData).putExtra("isEdit", true), 1310
            )
        }
        mBinding.btnAddMilestone.setOnClickListener {
            val params = HashMap<String, Any>()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                data.jobId?.let { it1 -> params.put("jobId", it1) }
                params.put("tradieId", data.tradieId.toString())

            } else {
                dataRecModel?.jobId?.let { it1 -> params.put("jobId", it1) }
                params.put("tradieId", dataRecModel?.tradieId.toString())
            }
            val milestones = ArrayList<Any>()
            var order = 1
            list.forEach {
                if (it.status == 5 || it.status == 4 || it.status == 0) {
                    if (it.milestoneId != null && it.milestoneId!!.length > 0) {
                        val milestoneData = MilestoneEditRequestData()
                        if (it.toDate == null || it.toDate?.length == 0)
                            milestoneData.to_date = it.fromDate.toString()
                        else
                            milestoneData.to_date = it.toDate.toString()
                        milestoneData.from_date = it.fromDate.toString()
                        milestoneData.isPhotoevidence = it.isPhotoevidence
                        milestoneData.recommended_hours = it.recommendedHours.toString()
                        milestoneData.milestone_name = it.milestoneName.toString()
                        milestoneData.order = order
                        milestoneData.milestoneId = it.milestoneId.toString()
                        milestones.add(milestoneData)
                    } else {
                        val milestoneData = MilestoneRequestData()
                        if (it.toDate == null || it.toDate?.length == 0)
                            milestoneData.to_date = it.fromDate.toString()
                        else
                            milestoneData.to_date = it.toDate.toString()
                        milestoneData.from_date = it.fromDate.toString()
                        milestoneData.isPhotoevidence = it.isPhotoevidence
                        milestoneData.recommended_hours = it.recommendedHours.toString()
                        milestoneData.milestone_name = it.milestoneName.toString()
                        milestoneData.order = order
                        milestones.add(milestoneData)
                    }
                }
                order++
            }
            deletedList.forEach {
                val milestoneData = MilestoneEditRequestData()
                if (it.toDate?.length == 0)
                    milestoneData.to_date = it.fromDate.toString()
                else
                    milestoneData.to_date = it.toDate.toString()
                milestoneData.from_date = it.fromDate.toString()
                milestoneData.isPhotoevidence = it.isPhotoevidence
                milestoneData.recommended_hours = it.recommendedHours.toString()
                milestoneData.milestone_name = it.milestoneName.toString()
                milestoneData.milestoneId = it.milestoneId.toString()
                milestoneData.isDeleteRequest = true
                milestones.add(milestoneData)
            }
            params.put("milestones", milestones)

            showAppPopupDialog(
                getString(R.string.are_you_want_to_change_request),
                getString(R.string.ok),
                getString(R.string.cancel),
                getString(R.string.change_request),
                {
                    mViewModel.changeMilestoneRequest(params)
                },
                {
                },
                true
            )
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)

    }

    private fun initRecyclerView() {
        mAdapter = RowEditMilestoneAdapter(list, object :
            RowEditMilestoneAdapter.OnMilestoneDataListner {
            override fun onEdit(position: Int) {
                val newData = ArrayList<MilestoneData>()

                list.forEach {
                    val milestone = it.milestoneName?.let { it1 ->
                        MilestoneData(
                            it1, it.isPhotoevidence, false,
                            it.fromDate.toString(),
                            it.toDate.toString(), it.recommendedHours.toString()
                        )
                    }
                    milestone?.let { it1 -> newData.add(it1) }
                }
                val currentData = list.get(position).milestoneName?.let { it1 ->
                    MilestoneData(
                        it1,
                        list.get(position).isPhotoevidence,
                        false,
                        list.get(position).fromDate.toString(),
                        list.get(position).toDate.toString(),
                        list.get(position).recommendedHours.toString()
                    )
                }

                startActivityForResult(
                    Intent(mBinding.rvMilestoneList.context, AddMilestoneActivity::class.java)
                        .putExtra("isEdit", true)
                        .putExtra("allData", newData)
                        .putExtra("data", currentData).putExtra("pos", position), 1310
                )
            }

            override fun onDelete(position: Int) {
                val dialog = Dialog(mBinding.rvMilestoneList.context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_popup)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
                title.text = getString(R.string.alert)

                val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
                msg.text = getString(R.string.milestone_delete)

                val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
                dialogBtn_okay.text = getString(R.string.yes)
                dialogBtn_okay.setOnClickListener {
                    dialog.dismiss()
                    isAlertShow = true
                    mBinding.btnAddMilestone.visibility = View.VISIBLE
                    deletedList.add(list.get(position))
                    list.removeAt(position)
                    mAdapter.notifyDataSetChanged()
                    if (list.size == 0) {
                        mBinding.llNoData.visibility = View.VISIBLE
                    }
                }

                val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
                dialogBtn_cancel.text = getString(R.string.no)
                dialogBtn_cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            override fun positionChanged() {
                mBinding.btnAddMilestone.visibility = View.VISIBLE
            }
        })
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvMilestoneList.layoutManager = layoutRecManager
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(list, mAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mBinding.rvMilestoneList)
        mBinding.rvMilestoneList.adapter = mAdapter
    }

    class SimpleItemTouchHelperCallback(
        val list: ArrayList<MilestoneList>,
        adapter: ItemTouchHelperAdapter
    ) :
        ItemTouchHelper.Callback() {
        private val mAdapter: ItemTouchHelperAdapter
        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return false
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {

            if (list.get(viewHolder.absoluteAdapterPosition).status == 0 ||
                list.get(viewHolder.absoluteAdapterPosition).status == 3 ||
                list.get(viewHolder.absoluteAdapterPosition).status == 4
            ) {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            } else {
                return 0
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            if (list.get(target.absoluteAdapterPosition).status == 0 ||
                list.get(target.absoluteAdapterPosition).status == 3 ||
                list.get(target.absoluteAdapterPosition).status == 4
            ) {
                mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            }
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        init {
            mAdapter = adapter
        }
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.JOB_MILESTONE_LIST -> {
                showToastShort(exception.message)
            }
            ApiCodes.CHANGE_MILESTONE_REQUEST -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.JOB_MILESTONE_LIST -> {
                mViewModel.mTradieJobMilestones.let {
                    if (it.milestones != null) {
                        for (data in it.milestones!!) {
                            if (data.status == MilestoneStatus.PENDING) {
                                data.markComplete = true
                                break
                            }
                        }
                        list.addAll(it.milestones!!)
                        mAdapter.notifyDataSetChanged()
                        if (list.size == 0) {
                            mBinding.llNoData.visibility = View.VISIBLE
                        } else {
                            mBinding.llNoData.visibility = View.GONE
                        }
                    }
                }
            }
            ApiCodes.CHANGE_MILESTONE_REQUEST -> {
                startActivityForResult(Intent(this, MilestoneSuccessActivity::class.java), 2610)
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
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
            window.setStatusBarColor(Color.WHITE)
        }
    }

    override fun onClick(p0: View?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataAll: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataAll)
        isAlertShow = true
        mBinding.llNoData.visibility = View.GONE
        if (resultCode == Activity.RESULT_OK && requestCode == 1310) {
            if (dataAll != null && dataAll.hasExtra("newData")) {
                mBinding.btnAddMilestone.visibility = View.VISIBLE
                val milestoneData = dataAll.getSerializableExtra("newData") as MilestoneData
                val pos = dataAll.getIntExtra("pos", -1)
                if (pos >= 0) {
                    list.get(pos).milestoneName = milestoneData.name
                    list.get(pos).recommendedHours = milestoneData.hours
                    list.get(pos).isPhotoevidence = milestoneData.photoRequired
                    list.get(pos).fromDate = milestoneData.start_date
                    list.get(pos).toDate = milestoneData.end_date
                    mAdapter.notifyItemChanged(pos)
                } else {
                    val milestoneItem = MilestoneList(
                        "",
                        milestoneData.name,
                        milestoneData.photoRequired,
                        milestoneData.start_date,
                        milestoneData.end_date,
                        "",
                        milestoneData.hours, false, 0, 0, 0.0, 0, 0, null, false
                    )
                    list.add(milestoneItem)
                    mAdapter.notifyItemInserted(list.size - 1)
                }
            }
        }

        if (requestCode == 2610 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBackPressed() {
        if (isNotShow) {
            super.onBackPressed()
        } else if (isAlertShow && list != null && list.size > 0) {

            val dialog = Dialog(mBinding.rvMilestoneList.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_popup)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
            title.text = getString(R.string.heads_up)

            val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
            msg.text = getString(R.string.milestone_error)

            val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
            dialogBtn_okay.text = getString(R.string.ok)
            dialogBtn_okay.setOnClickListener {
                dialog.dismiss()
                super.onBackPressed()
            }

            val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
            dialogBtn_cancel.text = getString(R.string.cancel)
            dialogBtn_cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } else {
            super.onBackPressed()
        }
    }


}