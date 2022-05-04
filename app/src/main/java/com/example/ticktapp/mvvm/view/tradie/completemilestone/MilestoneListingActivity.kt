package com.example.ticktapp.mvvm.view.tradie.completemilestone

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.model.jobmodel.JobMilestoneListModel
import com.app.core.preferences.PreferenceManager
import com.bumptech.glide.Glide
import com.exampl.UploadPhotosActivity
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowMilestoneListingAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieeMilestoneListBinding
import com.example.ticktapp.firebase.FirebaseDatabaseQueries
import com.example.ticktapp.firebase.FirebaseMessageListener
import com.example.ticktapp.firebase.FirebaseUserListener
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.jobmodel.MilestoneList
import com.app.core.util.*
import com.example.ticktapp.mvvm.view.tradie.*
import com.example.ticktapp.mvvm.viewmodel.MilestoneListViewModel
import java.util.*
import kotlin.collections.ArrayList

class MilestoneListingActivity : BaseActivity(), RowMilestoneListingAdapter.OnMilestoneDataListner,
    View.OnClickListener {
    private var needToSendResult: Boolean = false
    private var jobId: String? = null
    private var jobName: String? = null

    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityTradieeMilestoneListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MilestoneListViewModel::class.java) }
    private lateinit var mAdapter: RowMilestoneListingAdapter
    private val list by lazy { ArrayList<MilestoneList>() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradiee_milestone_list)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        jobId?.let { mViewModel.getJobMilestoneList(it) }

    }


    private fun getIntentData() {
        jobId = intent.getStringExtra("jobId")
        jobName = intent.getStringExtra("jobName")
        data = intent.getSerializableExtra("data") as JobRecModel
        mBinding.ilHeader.tvTitle.text = jobName
        mBinding.ilHeader.ivEdit.visibility = View.VISIBLE
        mBinding.ilHeader.ivBack.visibility = View.INVISIBLE
        mBinding.ilHeader.ivBack1.visibility = View.VISIBLE
    }

    private fun setUpListeners() {
        mBinding.ivJobDetail.setOnClickListener(this)
        mBinding.tvJobdetails.setOnClickListener(this)
        mBinding.ilHeader.ivEdit.setOnClickListener(this)
        mBinding.ilHeader.ivBack1.setOnClickListener(this)
        mBinding.llPostedBy.setOnClickListener(this)

        mBinding.userIvMsg.setOnClickListener {
            getLastMessages()
        }
    }

    private fun getLastMessages() {
        val inBoxMessage = ChatMessageBean()
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)

        inBoxMessage.messageRoomId =
            data?.jobId + "_" + loginUserId + "_" + data?.builderId

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
        chatModel.jobId = data?.jobId
        chatModel.jobName = data?.jobName
        if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
            chatModel.receiverId = data?.builderId
        if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
            chatModel.senderId = loginUserId
        data?.builderId?.let {
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
                                    this@MilestoneListingActivity,
                                    ChatTradieActivity::class.java
                                ).putExtra("data", chatModel)
                            )
                        } catch (e: Exception) {
                        }

                    }
                })
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)

    }

    private fun initRecyclerView() {
        mAdapter = RowMilestoneListingAdapter(list, this)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvMilestone.layoutManager = layoutRecManager
        mBinding.rvMilestone.adapter = mAdapter

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.JOB_MILESTONE_LIST -> {
                showToastShort(exception.message)

            }
        }
    }

    lateinit var myTradieJobMilestones: JobMilestoneListModel
    var position: Int = 0

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.JOB_MILESTONE_LIST -> {
                mViewModel.mTradieJobMilestones.let {
                    if (it.milestones != null) {
                        for (data in it.milestones!!) {
                            if (data.status == MilestoneStatus.PENDING || data.status == MilestoneStatus.DECLINED) {
                                data.markComplete = true
                                break
                            }
                        }
                        mAdapter.setdata(it.milestones!!)
                        myTradieJobMilestones = it

                        if (it.milestones?.get(0)?.declinedCount!! > 0) {
                            var count = 0
                            for (i in 0 until it.milestones!!.size) {
                                if (it.milestones?.get(i)?.declinedReason?.reason != null) {
                                    count++
                                }
                            }
                            mBinding.llDecline.visibility = View.VISIBLE
                            mBinding.tvDecline.text =
                                count.toString() + " " + getString(R.string.milestones_were_declined)
                        }

                    }
                    if (it.postedBy != null) {
                        mBinding.tvPostedBy.visibility = View.VISIBLE
                        mBinding.llPostedBy.visibility = View.VISIBLE
                        mBinding.clPostedby.visibility = View.VISIBLE
                        mBinding.tvJobUserTitle.text = it.postedBy.builderName
                        mBinding.tvJobUserDetails.text =
                            it.postedBy.ratings.toString() + ", " + it.postedBy.reviews
                        if (it.postedBy.reviews == 0 || it.postedBy.reviews == 1) {
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

                        Glide.with(mBinding.root.context).load(it.postedBy.builderImage)
                            .placeholder(R.drawable.placeholder_profile)
                            .error(R.drawable.placeholder_profile)
                            .into(mBinding.ivJobUserProfile)
                    } else {
                        mBinding.tvPostedBy.visibility = View.GONE
                        mBinding.llPostedBy.visibility = View.GONE
                        mBinding.clPostedby.visibility = View.GONE
                    }

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

    override fun onMarkComplete(position: Int) {
        this.position = position
        val isJobComplete: Boolean = position == mAdapter.itemCount - 1
        if (mAdapter.mItems.get(position).isPhotoevidence) {
            intent = Intent(this, UploadPhotosActivity::class.java)
        } else {
            intent = Intent(this, MilestoneHoursActivity::class.java)
        }
        intent.putExtra(IntentConstants.JOB_ID, jobId).putExtra(IntentConstants.JOB_NAME, jobName)
            .putExtra(IntentConstants.MILESTONE_ID, mAdapter.mItems.get(position).milestoneId)
            .putExtra(IntentConstants.IS_JOB_COMPLETED, isJobComplete)
            .putExtra(IntentConstants.CATEGORY, data.tradeName)
            .putExtra(IntentConstants.MILESTONE_NUMBER, data.milestoneNumber)
            .putExtra(
                IntentConstants.JOB_COUNT,
                mAdapter.mItems.get(position).jobCompletedCount.toString()
            )
            .putExtra(IntentConstants.AMOUNT, mAdapter.mItems.get(position).amount.toString())
            .putExtra(IntentConstants.PAY_TYPE, mAdapter.mItems.get(position).pay_type)
            .putExtra(IntentConstants.MILESTONE_COUNT, mAdapter.itemCount.toString())
        resultLauncher.launch(intent)


    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    if (!data.getBooleanExtra(IntentConstants.IS_JOB_COMPLETED, false)) {
                        jobId?.let {
                            mViewModel.getJobMilestoneList(it)
                            needToSendResult = true
                        }
                    } else {
                        setResult(RESULT_OK, data)
                        finish()
                    }
                }
            }
        }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.tvJobdetails -> {
                val intent = Intent(this, TradieJobDetailActivity::class.java)
                intent.putExtra("data", data).putExtra("showMilestoneProgress", true)
                    .putExtra("showEditOption", false)
                startActivity(intent)

            }
            mBinding.ivJobDetail -> {
                mBinding.tvJobdetails.performClick()
            }
            mBinding.ilHeader.ivEdit -> {
                showMenu(mBinding.ilHeader.ivEdit)
            }
            mBinding.ilHeader.ivBack1 -> {
                onBackPressed()
            }
            mBinding.llPostedBy -> {
                startActivity(
                    Intent(this, BuilderProfileActivity::class.java)
                        .putExtra("data", data)
                )
            }
        }
    }

    override fun onBackPressed() {
        if (needToSendResult) {
            setResult(RESULT_OK)
        }
        finish()

    }

    private fun showMenu(view: View) {
        val viewGroup = view.findViewById<LinearLayout>(R.id.ll_popup)
        val layoutInflater =
            (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val layout: View =
            layoutInflater.inflate(R.layout.layout_pop_up_bank_details_edit, viewGroup)
        val popup = PopupWindow(view.context)
        popup.contentView = layout
        popup.setBackgroundDrawable(ColorDrawable())
        popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        val tvOptionOne = layout.findViewById<TextView>(R.id.tv_option_one)
        val tvOptionTwo = layout.findViewById<TextView>(R.id.tv_option_two)
        tvOptionOne.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_comment, 0, 0, 0)
        tvOptionTwo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_cancel_filled,
            0,
            0,
            0
        )
        tvOptionOne.text = view.context.getString(R.string.lodge_dispute)
        tvOptionTwo.text = view.context.getString(R.string.cancel_job)
        tvOptionOne.setOnClickListener {
            startActivity(
                Intent(this, LodgeDisputeTradieActivity::class.java)
                    .putExtra(IntentConstants.JOB_ID, jobId)
                    .putExtra(IntentConstants.JOB_NAME, jobName)
            )
            popup.dismiss()

        }
        tvOptionTwo.setOnClickListener {
            popup.dismiss()
            val intent = Intent(this, CancelJobByTradieActivity::class.java)
            intent.putExtra(IntentConstants.JOB_ID, jobId)
            intent.putExtra(IntentConstants.JOB_NAME, jobName)
            resultLauncher.launch(intent)
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


}