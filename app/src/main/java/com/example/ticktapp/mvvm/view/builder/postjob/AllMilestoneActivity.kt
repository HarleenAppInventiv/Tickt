package com.example.ticktapp.mvvm.view.builder.postjob

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exampl.VideoImageActivity
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowAddMilestoneAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneListBinding
import com.example.ticktapp.model.MilestoneData
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.util.*


class AllMilestoneActivity : BaseActivity() {
    private var isReturn: Boolean = false
    private lateinit var mRecAdapter: RowAddMilestoneAdapter
    private lateinit var mBinding: ActivityMilestoneListBinding
    private lateinit var data: ArrayList<MilestoneData>
    private var rData: JobRecModelRepublish? = null
    var startDate = ""
    var endDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_milestone_list)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setUpData()
        listener()
    }

    private fun getIntentData() {
         if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE).isNullOrEmpty()){
            data=PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE)?.getList<MilestoneData>()?: ArrayList()
        }else if (intent.hasExtra("data"))
         {
             data = intent.getSerializableExtra("data") as ArrayList<MilestoneData>
         }

        if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE).isNullOrEmpty()){
            startDate =
                PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE)?:""
        }else if (intent.hasExtra("start_date")) {
            startDate = intent.extras!!.getString("start_date", "")
        }
        if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_END_DATE).isNullOrEmpty()){

            endDate = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_END_DATE)?:""
        }else if (intent.hasExtra("end_date")) {
            endDate = intent.extras!!.getString("end_date", "")
        }

//        if (PostJobData.postjobDataNullCheck()) {
//            data.addAll(PostJobData.getMileStoneList())
//        }
        if (intent.hasExtra("isReturn")) {
            isReturn = intent.getBooleanExtra("isReturn", false)
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .isNullOrEmpty()
        ) {
            rData = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .getPojoData(JobRecModelRepublish::class.java)}
        else if (intent.hasExtra("rData") && intent.getSerializableExtra("rData") != null) {
            rData = intent.getSerializableExtra("rData") as JobRecModelRepublish
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setUpData() {
        val layoutManager = LinearLayoutManager(this)
        mRecAdapter =
            RowAddMilestoneAdapter(data, object : RowAddMilestoneAdapter.OnMilestoneDataListner {
                override fun onEdit(position: Int) {
                    editMilestone(position)
                }

                override fun onDelete(position: Int) {
                    deleteMilestone(position)
                }
            })
        mBinding.rvMilestoneList.layoutManager = layoutManager
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(mRecAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mBinding.rvMilestoneList)
        mBinding.rvMilestoneList.adapter = mRecAdapter
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun listener() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.tvSaveTemplate.setOnClickListener {
            startActivity(Intent(this, SaveTemplateActivity::class.java).putExtra("data", data))
        }
        mBinding.btnAddMilestone.setOnClickListener {
            if (isDateLies()) {
                PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE,data.toJsonString())
                if (isReturn) {
                    setResult(Activity.RESULT_OK, Intent().putExtra("mData", data))
                    finish()
                } else {
                    PostJobData.setPostNewJobActivityData(arrayListOf())

                    startActivity(
                        Intent(this, VideoImageActivity::class.java)/*.putExtra(
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
                            .putExtra(
                                "amount",
                                intent.getStringExtra("amount")
                            ).putExtra(
                                "isSearchType",
                                intent.getIntExtra("isSearchType", 1)
                            ).putExtra(
                                "isJobType",
                                intent.getIntExtra("isJobType", -1)
                            ).putExtra("start_date", intent.getStringExtra("start_date"))
                            .putExtra("end_date", intent.getStringExtra("end_date"))
                            .putExtra("mData", data)
                            .putExtra("rData", rData)*/
                    )
                }
            }
        }
        mBinding.jobAddMilestone.setOnClickListener {
            PostJobData.setPostNewJobActivityData(arrayListOf())
            PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE,data.toJsonString())
            startActivityForResult(
                Intent(
                    this,
                    AddMilestoneActivity::class.java
                )/*.putExtra("allData", data).putExtra(
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
                    .putExtra(
                        "amount",
                        intent.getStringExtra("amount")
                    ).putExtra(
                        "isSearchType",
                        intent.getIntExtra("isSearchType", 1)
                    ).putExtra(
                        "isJobType",
                        intent.getIntExtra("isJobType", -1)
                    ).putExtra("start_date", intent.getStringExtra("start_date"))
                    .putExtra("end_date", intent.getStringExtra("end_date"))
                    .putExtra("rData", rData)*/, 1310
            )
        }
    }

    private fun isDateLies(): Boolean {

        val start = DateUtils.getCalendarFromDate(DateUtils.DATE_FORMATE_8, startDate)
        val end = DateUtils.getCalendarFromDate(DateUtils.DATE_FORMATE_8, endDate)

        data.forEach {
            val inStart = DateUtils.getCalendarFromDate(
                DateUtils.DATE_FORMATE_8,
                it.start_date
            )
            if (start == null || inStart == null || end == null) {
                showToastShort(getString(R.string.milestone_date_lies))
                return false
            }

            if (start != inStart && start.after(inStart)) {
                showToastShort(getString(R.string.milestone_date_lies))
                return false
            }
            if (start != end) {
                if (it.end_date.isEmpty() && inStart.after(end)) {
                    showToastShort(getString(R.string.milestone_date_lies))
                    return false
                }
            }
            if (it.end_date.length > 0) {
                val inEnd = DateUtils.getCalendarFromDate(
                    DateUtils.DATE_FORMATE_8,
                    it.end_date
                )
                if (inEnd == null) {
                    showToastShort(getString(R.string.milestone_date_lies))
                    return false
                }
                /*if (start != end) {
                    if (end != inEnd && inEnd.after(end)) {
                        showToastShort(getString(R.string.milestone_date_lies))
                        return false
                    }
                }*/
            }
        }
        return true
    }

    private fun editMilestone(pos: Int) {
        PostJobData.setPostNewJobActivityData(arrayListOf())
        startActivityForResult(


            Intent(this, AddMilestoneActivity::class.java).putExtra(
                "allData",
                data
            ).putExtra("data", data.get(pos)).putExtra("pos", pos)/*.putExtra(
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
                .putExtra(
                    "amount",
                    intent.getStringExtra("amount")
                ).putExtra(
                    "isSearchType",
                    intent.getIntExtra("isSearchType", 1)
                ).putExtra(
                    "isJobType",
                    intent.getIntExtra("isJobType", -1)
                ).putExtra("start_date", intent.getStringExtra("start_date"))
                .putExtra("end_date", intent.getStringExtra("end_date")).putExtra("rData", rData)*/,
            1310
        )
    }

    private fun deleteMilestone(pos: Int) {
        val dialog = Dialog(this)
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
            data.removeAt(pos)
            PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE,data.toJsonString())

            mRecAdapter.notifyDataSetChanged()
            if (data.size == 0) {
                startActivity(
                    Intent(this, TemplateMilestoneActivity::class.java)
                      /*  .putExtra(
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
                        .putExtra(
                            "amount",
                            intent.getStringExtra("amount")
                        ).putExtra(
                            "isSearchType",
                            intent.getIntExtra("isSearchType", 1)
                        ).putExtra(
                            "isJobType",
                            intent.getIntExtra("isJobType", -1)
                        ).putExtra("start_date", intent.getStringExtra("start_date"))
                        .putExtra("end_date", intent.getStringExtra("end_date"))*/
                )
                finish()
            }
        }
        val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
        dialogBtn_cancel.text = getString(R.string.no)
        dialogBtn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    class SimpleItemTouchHelperCallback(adapter: ItemTouchHelperAdapter) :
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
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataAll: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataAll)
        if (resultCode == Activity.RESULT_OK && requestCode == 1310) {
            if (dataAll != null && dataAll.hasExtra("allData")) {
                data.clear()
                data.addAll(dataAll.getSerializableExtra("allData") as ArrayList<MilestoneData>)
                mRecAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isReturn) {
            super.onBackPressed()
        } else {
            /*   if(){

               }else{

               }*/
            PostJobData.setPostNewJobActivityData(
                data,
                jobTypePayment = if (intent.hasExtra("isJobType"))intent.getIntExtra("isJobType", 1) else PreferenceManager.getInt(PreferenceManager.NEW_JOB_PREF.JOB_PAY_TYPE,-1
                ),
                endDate = endDate,
            )
            super.onBackPressed()

            /*  if (data != null && data.size > 0) {
                  val dialog = Dialog(this)
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
                      PostJobData.setPostNewJobActivityData(data)
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
              }*/
        }
    }
}