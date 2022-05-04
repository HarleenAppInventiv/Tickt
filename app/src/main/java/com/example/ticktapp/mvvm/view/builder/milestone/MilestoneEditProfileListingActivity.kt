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
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowEditMilestoneProfileAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneListProfileBinding
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.model.MilestoneEditOnlyRequestData
import com.example.ticktapp.model.TemplateMilestoneData
import com.app.core.model.jobmodel.MilestoneList
import com.example.ticktapp.mvvm.view.builder.postjob.AddMilestoneActivity
import com.example.ticktapp.mvvm.view.builder.postjob.SavedTemplateActivity
import com.example.ticktapp.mvvm.viewmodel.MilestoneListViewModel

class MilestoneEditProfileListingActivity : BaseActivity(),
    View.OnClickListener {
    private lateinit var data: TemplateMilestoneData
    private var isAlertShow: Boolean = false
    private lateinit var mBinding: ActivityMilestoneListProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MilestoneListViewModel::class.java) }
    private lateinit var mAdapter: RowEditMilestoneProfileAdapter
    private var list = ArrayList<MilestoneList>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_milestone_list_profile)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
    }


    private fun getIntentData() {
        if (intent.hasExtra("data")) {
            data = intent.getSerializableExtra("data") as TemplateMilestoneData
            data.milestones.forEach {
                list.add(
                    MilestoneList(
                        it.milestoneId,
                        it.milestoneName,
                        it.isPhotoevidence,
                        it.fromDate,
                        it.toDate,
                        "",
                        it.recommendedHours
                    )
                )
            }
            mAdapter.notifyDataSetChanged()
            if (list.size == 0) {
                mBinding.llNoData.visibility = View.VISIBLE
            } else {
                mBinding.llNoData.visibility = View.GONE
            }
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
            params.put("templateId", data.tempId)
            params.put("template_name", data.templateName)
            val milestones = ArrayList<MilestoneEditOnlyRequestData>()
            var order = 1
            list.forEach {
                if (it.status == 3 || it.status == 4 || it.status == 0) {
                    if (it.milestoneId != null && it.milestoneId!!.length > 0) {
                        val milestoneData = MilestoneEditOnlyRequestData()
                        milestoneData.to_date = it.toDate.toString()
                        milestoneData.from_date = it.fromDate.toString()
                        milestoneData.isPhotoevidence = it.isPhotoevidence
                        milestoneData.recommended_hours = it.recommendedHours.toString()
                        milestoneData.milestone_name = it.milestoneName.toString()
                        milestoneData.order = order
                        milestoneData.milestoneId = it.milestoneId.toString()
                        milestones.add(milestoneData)
                    } else {
                        val milestoneData = MilestoneEditOnlyRequestData()
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
            params.put("milestones", milestones)
            mViewModel.editMilestoneRequest(params)
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = RowEditMilestoneProfileAdapter(list, object :
            RowEditMilestoneProfileAdapter.OnMilestoneDataListner {
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
            ApiCodes.EDIT_MILESTONE_REQUEST -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {

            ApiCodes.EDIT_MILESTONE_REQUEST -> {
                mViewModel.tempMilestoneDetails.let {
                    data = it
                    startActivityForResult(Intent(this, SavedTemplateActivity::class.java), 2610)
                }
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
            val newData = Intent()
            newData.putExtra("data", data)
            setResult(Activity.RESULT_OK, newData)
            finish()
        }
    }

    override fun onBackPressed() {
        if (isAlertShow && list != null && list.size > 0) {
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