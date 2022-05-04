package com.example.ticktapp.mvvm.view.builder.postjob

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowMilestoneApproveBuilderAdapter
import com.example.ticktapp.adapters.RowMilestoneApproveBuilderAdapter.OnMilestoneClickListener
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityCheckApproveMilestoneBinding
import com.example.ticktapp.firebase.FirebaseDatabaseQueries
import com.example.ticktapp.firebase.FirebaseMessageListener
import com.example.ticktapp.firebase.FirebaseUserListener
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobMilestStonRespnse
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.builder.CancelJobByBuilderActivity
import com.example.ticktapp.mvvm.view.builder.ChatBuilderActivity
import com.example.ticktapp.mvvm.view.builder.LodgeDisputeBuilderActivity
import com.example.ticktapp.mvvm.view.builder.TradieProfileActivity
import com.example.ticktapp.mvvm.view.builder.milestone.MilestoneDetailsActivity
import com.example.ticktapp.mvvm.view.builder.milestone.MilestoneEditListingActivity
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import java.util.*


class CheckAndApproveMilestoneActivity : BaseActivity() {
    private var isDisputed: Boolean? = false
    private lateinit var mRecAdapter: RowMilestoneApproveBuilderAdapter
    private lateinit var mBinding: ActivityCheckApproveMilestoneBinding
    private var data: JobDashboardModel? = null
    private var dataRecModel: JobRecModel? = null
    private var jobMilestondData: JobMilestStonRespnse? = null
    private var isChat: Boolean = false

    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_check_approve_milestone)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()

    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun refreshData(newIntent: Intent) {
        super.refreshData(newIntent)
        runOnUiThread {
            if (intent.getSerializableExtra("data") is JobRecModel) {
                dataRecModel?.jobId?.let { mViewModel.milestoneLists(it) }
            } else {
                data?.jobId?.let { mViewModel.milestoneLists(it) }
            }
        }
    }

    private fun getIntentData() {
        isChat = intent.getBooleanExtra("isChat", false)
        if (intent.getSerializableExtra("data") is JobRecModel) {
            dataRecModel = intent.getSerializableExtra("data") as JobRecModel
            mBinding.llMain.visibility = View.GONE
            dataRecModel?.jobId?.let { mViewModel.milestoneLists(it) }
        } else {
            data = intent.getSerializableExtra("data") as JobDashboardModel
            mBinding.llMain.visibility = View.GONE
            data?.jobId?.let { mViewModel.milestoneLists(it) }
        }
        if (isChat) {
            mBinding.userIvMsg.visibility = View.VISIBLE
        } else {
            mBinding.userIvMsg.visibility = View.GONE
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setUpData(jobMilestStonRespnse: JobMilestStonRespnse) {
        jobMilestondData = jobMilestStonRespnse
        isDisputed = jobMilestStonRespnse.dispute
        val layoutManager = LinearLayoutManager(this)
        mRecAdapter =
            RowMilestoneApproveBuilderAdapter(
                jobMilestStonRespnse.milestones,
                object : OnMilestoneClickListener {
                    override fun onCheckApprove(pos: Int, isApproved: Boolean) {
                        startActivityForResult(
                            Intent(
                                mBinding.rvMilestoneList.context,
                                MilestoneDetailsActivity::class.java
                            ).putExtra("data", jobMilestStonRespnse)
                                .putExtra("pos", pos), 1310
                        )

                    }
                })
        mBinding.rvMilestoneList.layoutManager = layoutManager
        mBinding.rvMilestoneList.adapter = mRecAdapter
        mBinding.tvJobNameTitle.text = jobMilestStonRespnse.jobName
        mBinding.tvJobUserTitle.text = jobMilestStonRespnse.tradie?.tradieName
        Glide.with(mBinding.root.context).load(jobMilestStonRespnse.tradie?.tradieImage)
            .placeholder(R.drawable.placeholder_profile)
            .error(R.drawable.placeholder_profile)
            .into(mBinding.ivJobUserProfile)
        mBinding.tvJobUserDetails.text =
            jobMilestStonRespnse.tradie?.ratings.toString() + ", " + jobMilestStonRespnse.tradie?.reviews
        if (jobMilestStonRespnse.tradie?.reviews == 0 || jobMilestStonRespnse.tradie?.reviews == 1) {
            mBinding.tvJobUserDetails.append(
                " " + getString(
                    R.string.review
                )
            )
        } else {
            mBinding.tvJobUserDetails.append(
                " " + getString(
                    R.string.reviews
                )
            )
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

    private fun listener() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.icPencilEdit.setOnClickListener { showMenu(it) }
        mBinding.tvJobDetails.setOnClickListener {
            if (data != null) {
                startActivity(
                    Intent(
                        this,
                        JobDetailsActivity::class.java
                    ).putExtra("data", data)
                        .putExtra("isBuilder", intent.getBooleanExtra("isBuilder", false))
                        .putExtra("isEdit", false)
                        .putExtra("isDisputed", isDisputed)
                        .putExtra("isChat", isChat)
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        JobDetailsActivity::class.java
                    ).putExtra("data", dataRecModel)
                        .putExtra("isBuilder", intent.getBooleanExtra("isBuilder", false))
                        .putExtra("isEdit", false)
                        .putExtra("isDisputed", isDisputed)
                        .putExtra("isChat", isChat)
                )
            }
        }
        mBinding.userIvRedirection.setOnClickListener {
            if (data != null) {
                if (jobMilestondData != null && (data?.tradieId == null || data?.tradieId?.length!! == 0)) {
                    data?.tradieId = jobMilestondData?.tradie?.tradieId
                }
                startActivity(
                    Intent(
                        this,
                        TradieProfileActivity::class.java
                    ).putExtra("data", data)
                        .putExtra("isBuilder", intent.getBooleanExtra("isBuilder", false))
                        .putExtra("isChat", isChat)
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        TradieProfileActivity::class.java
                    ).putExtra("data", dataRecModel)
                        .putExtra("isBuilder", intent.getBooleanExtra("isBuilder", false))
                        .putExtra("isChat", isChat)
                )
            }
        }

        mBinding.userIvMsg.setOnClickListener {
            getLastMessages()
        }
    }

    private fun getLastMessages() {
        val inBoxMessage = ChatMessageBean()
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)

        if (intent.getSerializableExtra("data") is JobRecModel) {
            inBoxMessage.messageRoomId =
                dataRecModel?.jobId + "_" + dataRecModel?.tradieId + "-" + loginUserId
        } else {
            inBoxMessage.messageRoomId = data?.jobId + "_" + data?.tradieId + "_" + loginUserId
        }
        FirebaseDatabaseQueries.instance?.getLastMessageInfo(
            null,
            inBoxMessage,
            object : FirebaseMessageListener {
                override fun getMessages(message: ChatMessageBean?) {
                    getUsersData(message)
                }

                override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {}
                override fun newMessagesListing() {

                }

                override fun noData() {
                    getUsersData(null)
                }
            }
        )
    }

    private fun getUsersData(chatModels: ChatMessageBean?) {
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        var chatModel = ChatMessageBean();
        if (chatModels != null) {
            chatModel = chatModels
        }
        if (intent.getSerializableExtra("data") is JobRecModel) {
            chatModel.jobId = dataRecModel?.jobId
            chatModel.jobName = dataRecModel?.jobName
            if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
                chatModel.receiverId = dataRecModel?.tradieId
            if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
                chatModel.senderId = loginUserId
            dataRecModel?.tradieId?.let {
                FirebaseDatabaseQueries.instance?.getUser(
                    it,
                    object : FirebaseUserListener {
                        override fun getUser(user: UserBean?) {
                            try {
                                user?.let {
                                    // setting image
                                    if (!it.image.isNullOrEmpty()) {
                                        chatModel?.senderImage = it.image!!
                                    }

                                    if (!it.name.isNullOrEmpty()) {
                                        chatModel?.senderName = it.name
                                    }
                                    if (it.userType != null) {
                                        chatModel?.senderType = it.userType.toString()
                                    }
                                    if (!it.image.isNullOrEmpty()) {
                                        chatModel?.senderImage = it.image
                                    }

                                }
                                startActivity(
                                    Intent(
                                        mBinding.rvMilestoneList.context,
                                        ChatBuilderActivity::class.java
                                    ).putExtra("data", chatModel)
                                        .putExtra("senderName", mBinding.tvJobUserTitle.text)
                                )
                            } catch (e: Exception) {
                            }

                        }
                    })
            }
        } else {
            chatModel.jobId = data?.jobId
            chatModel.jobName = data?.jobName
            if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
                chatModel.receiverId = data?.tradieId
            if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
                chatModel.senderId = loginUserId
            data?.tradieId?.let {
                FirebaseDatabaseQueries.instance?.getUser(
                    it,
                    object : FirebaseUserListener {
                        override fun getUser(user: UserBean?) {
                            try {
                                user?.let {
                                    // setting image
                                    if (!it.image.isNullOrEmpty()) {
                                        chatModel?.senderImage = it.image!!
                                    }

                                    if (!it.name.isNullOrEmpty()) {
                                        chatModel?.senderName = it.name
                                    }
                                    if (it.userType != null) {
                                        chatModel?.senderType = it.userType.toString()
                                    }
                                    if (!it.image.isNullOrEmpty()) {
                                        chatModel?.senderImage = it.image
                                    }
                                    startActivity(
                                        Intent(
                                            mBinding.rvMilestoneList.context,
                                            ChatBuilderActivity::class.java
                                        ).putExtra("data", chatModel)
                                            .putExtra("senderName", mBinding.tvJobUserTitle.text)
                                    )
                                }
                            } catch (e: Exception) {
                            }
                        }
                    })
            }
        }


    }

    private fun showMenu(view: View) {
        val viewGroup = view.findViewById<LinearLayout>(R.id.ll_popup)
        val layoutInflater =
            (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val layout: View =
            layoutInflater.inflate(R.layout.layout_pop_up_milestone_option, viewGroup)
        val popup = PopupWindow(view.context)
        popup.contentView = layout
        popup.setBackgroundDrawable(ColorDrawable())
        popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        val tvOptionMain = layout.findViewById<TextView>(R.id.tv_option_main)
        val tvOptionOne = layout.findViewById<TextView>(R.id.tv_option_one)
        val tvOptionTwo = layout.findViewById<TextView>(R.id.tv_option_two)
        tvOptionOne.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lounge, 0, 0, 0)
        tvOptionTwo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_cancel_filled,
            0,
            0,
            0
        )
        tvOptionMain.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_edit_milestone,
            0,
            0,
            0
        )
//        if (isDisputed == true) {
//            tvOptionOne.visibility = View.GONE
//        }
        tvOptionMain.text = view.context.getString(R.string.edit_milestone)
        tvOptionOne.text = view.context.getString(R.string.lodge_dispute)
        tvOptionTwo.text = view.context.getString(R.string.cancel_job)
        tvOptionOne.setOnClickListener {
            popup.dismiss()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                startActivity(
                    Intent(this, LodgeDisputeBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, dataRecModel?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, dataRecModel?.jobName)
                )
            } else {
                startActivity(
                    Intent(this, LodgeDisputeBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, data?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, data?.jobName)
                )
            }
        }
        tvOptionTwo.setOnClickListener {
            popup.dismiss()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                startActivity(
                    Intent(this, CancelJobByBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, dataRecModel?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, dataRecModel?.jobName)
                )
            } else {
                startActivity(
                    Intent(this, CancelJobByBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, data?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, data?.jobName)
                )
            }
        }
        tvOptionMain.setOnClickListener {
            popup.dismiss()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                startActivity(
                    Intent(
                        this,
                        MilestoneEditListingActivity::class.java
                    ).putExtra("data", dataRecModel).putExtra("isNotShow", true)
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        MilestoneEditListingActivity::class.java
                    ).putExtra("data", data).putExtra("isNotShow", true)
                )
            }

        }
        popup.setTouchInterceptor { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popup.dismiss()
                return@setTouchInterceptor true
            }
            false
        }
        popup.animationStyle = R.style.popupWindowAnim
        popup.showAtLocation(layout, Gravity.TOP or Gravity.END, 50, 280)

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.MILESTONE_LIST -> {
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.MILESTONE_LIST -> {
                mBinding.llMain.visibility = View.VISIBLE
                mViewModel.jobMilestStonRespnse.let {
                    setUpData(it)
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, datas: Intent?) {
        super.onActivityResult(requestCode, resultCode, datas)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (intent.getSerializableExtra("data") is JobRecModel) {
                dataRecModel?.jobId?.let { mViewModel.milestoneLists(it) }
            } else {
                data?.jobId?.let { mViewModel.milestoneLists(it) }
            }
        }
    }


}