package com.example.ticktapp.mvvm.view.tradie

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.preferences.PreferenceManager
import com.app.core.util.FireStore
import com.example.ticktapp.R
import com.example.ticktapp.adapters.TradieMessagesAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentTradieMessageBinding
import com.example.ticktapp.firebase.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class TradieMessageFragment : BaseFragment(), View.OnClickListener, FirebaseMessageListener,
    FirebaseCallbackListener {

    private var msgListener: ValueEventListener? = null
    private var msgPath: DatabaseReference? = null
    private lateinit var mBinding: FragmentTradieMessageBinding
    private lateinit var mRootView: View
    private var mAdapter: TradieMessagesAdapter? = null
    private var messageList: ArrayList<ChatMessageBean> = ArrayList()
    private var mainMessageList: ArrayList<ChatMessageBean> = ArrayList()
    private var mCurrentUserId: String? = null
    private var mCurrentUserEmail: String? = null
    private var mEventCalledFirstTime = true

    companion object {
        fun getInstance(): TradieMessageFragment {
            val fragment = TradieMessageFragment()
            return fragment
        }

        var isChangeOccured = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tradie_message, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    override fun onResume() {
        super.onResume()
        if (isChangeOccured)
            getLastChatMessagesList()
        if (msgListener != null && msgPath != null) {
            msgPath!!.addValueEventListener(msgListener!!)
        } else {
            checkForNewChatMessages()
        }
    }

    override fun initialiseFragmentBaseViewModel() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        setListener()
        updateFirebaseUserData()
//        checkForNewChatMessages()
        mBinding.swipeMessage.isRefreshing = false
        getLastChatMessagesList()
    }

    private fun init() {
        mCurrentUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        mCurrentUserEmail = PreferenceManager.getString(PreferenceManager.EMAIL)
        mainMessageList = ArrayList()
        messageList = ArrayList()
        mBinding.rvMessages.layoutManager = LinearLayoutManager(context)
        setAdapter()
    }

    private fun updateFirebaseUserData() {
        if (mCurrentUserEmail != null && mCurrentUserEmail?.length!! > 0) {
            FirebaseDatabaseQueries.instance?.signInFirebaseDatabase(
                mCurrentUserEmail,
                FireStore.FireStoreConstPassword.DEFAULT_PASS,
                object : FirebaseAuthListener {
                    override fun onAuthSuccess(task: Task<AuthResult?>?, user: FirebaseUser?) {
                        mBinding.swipeMessage.isRefreshing = false
                        FirebaseDatabaseQueries.instance?.updateUser(requireContext() as HomeActivity)
                    }

                    override fun onAuthError(task: Task<AuthResult?>?) {
                        signUpUserInFirebase(mCurrentUserEmail!!)
                    }
                })
        }
    }

    private fun signUpUserInFirebase(email: String) {
        FirebaseDatabaseQueries.instance?.createUserInFirebaseDatabase(
            email,
            FireStore.FireStoreConstPassword.DEFAULT_PASS,
            object : FirebaseAuthListener {
                override fun onAuthSuccess(task: Task<AuthResult?>?, user: FirebaseUser?) {
                    mBinding.swipeMessage.isRefreshing = false
                    FirebaseDatabaseQueries.instance?.updateUser(requireContext() as HomeActivity)
                }

                override fun onAuthError(task: Task<AuthResult?>?) {
                    mBinding.swipeMessage.isRefreshing = false
                }
            })
    }

    private fun getUnreadMessagesCount(messageList: ArrayList<ChatMessageBean>?) {
        var unreadMessages = 0
        for (i in messageList!!.indices) {
            unreadMessages += Integer.parseInt(messageList[i].unreadMessages.toString())
        }
//        activity?.let {
//            (it as HomeActivity).unReadMessagesCount(unreadMessages)
//            }
    }

    private fun setListener() {

        mBinding.swipeMessage.setOnRefreshListener {
            getLastChatMessagesList()
        }
        mBinding.ivSearch.setOnClickListener {
            mBinding.llCvSearch.visibility = View.VISIBLE
        }
        mBinding.tvSearchClose.setOnClickListener {
            mBinding.edSearch.setText("")
            mBinding.llCvSearch.visibility = View.GONE
        }
        mBinding.ivSearchClose.setOnClickListener {
            mBinding.edSearch.setText("")
        }
        mBinding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mAdapter?.getFilter()?.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }


    private fun setAdapter() {
        mAdapter?.notifyDataSetChanged() ?: run {
            mAdapter = TradieMessagesAdapter(messageList!!)
            mAdapter!!.setContext(this)
            mBinding.rvMessages.adapter = mAdapter
        }

    }

    /*  private fun manageUI() {
          tvNoDataFound.text = getString(R.string.no_new_messsage_found)
      }*/

    private fun getLastChatMessagesList() {
//        messageList?.clear()
//        mainMessageList?.clear()
//        mAdapter?.notifyDataSetChanged()
//        mAdapter=null
        FirebaseDatabaseQueries.instance?.getInboxMessagesList(mCurrentUserId, this)
//        FirebaseDatabaseQueries.instance?.getInboxMessagesList(
//            userId = mCurrentUserId,
//            messageListener = object : FirebaseMessageListener {
//                override fun getMessages(message: ChatMessageBean?) {
//
//                }
//
//                override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {
//                    if (messagesList?.isNotEmpty() == true) {
//                        getLastMessages(messagesList)
//                    } else {
//                        mBinding.swipeMessage.isRefreshing = false
//                        manageRecyclerViewVisibility()
//                    }
//
//                }
//
//                override fun newMessagesListing() {
//
//                }
//
//                override fun noData() {
//                }
//            })
    }

    private fun checkForNewChatMessages() {
        FirebaseDatabaseQueries.instance?.checkInboxNewMessagesList(
            userId = mCurrentUserId, this, this
        )
//        FirebaseDatabaseQueries.instance?.checkInboxNewMessagesList(
//            userId = mCurrentUserId,
//            messageListener = object : FirebaseMessageListener {
//                override fun getMessages(message: ChatMessageBean?) {
//
//                }
//
//                override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {
//
//                }
//
//                override fun newMessagesListing() {
//
//                    if (mEventCalledFirstTime) {
//                        //Log.e("is_new_message_arrived","yes")
//                        mEventCalledFirstTime = false
//                    } else
//                        getLastChatMessagesList()
//
//                }
//
//                override fun noData() {
//                }
//            })
    }

    private fun getLastMessages(roomIdList: List<ChatMessageBean?>) {
        messageList?.clear()

        mainMessageList?.clear()
//        mAdapter?.notifyDataSetChanged()
//        mAdapter=null

        for (inBoxMessage in roomIdList) {
            FirebaseDatabaseQueries.instance?.getLastMessageInfo(
                null,
                inBoxMessage, this
            )
//
//            FirebaseDatabaseQueries.instance?.getLastMessageInfo(
//                null,
//                inBoxMessage,
//                object : FirebaseMessageListener {
//                    override fun getMessages(message: ChatMessageBean?) {
//                        mBinding.swipeMessage.isRefreshing = false
//                        messageList?.add(message!!)
//                        mainMessageList?.add(message!!)
//                        if (messageList?.size == mainMessageList?.size) {
//                            messageList?.forEachIndexed { pos, it ->
//                                if (it.jobId == null || it.jobId!!.length == 0) {
//                                    messageList?.get(pos)?.jobId = mainMessageList?.get(pos)?.jobId
//                                    messageList?.get(pos)?.jobName =
//                                        mainMessageList?.get(pos)?.jobName
//                                }
//                            }
//                        }
//                        getUsersData()
//                    }
//
//                    override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {}
//                    override fun newMessagesListing() {
//
//                    }
//
//                    override fun noData() {
//                    }
//                }
//            )
        }
    }


    private fun getUsersData() {
        for (i in 0 until messageList!!.size) {
            val chatBean = messageList!![i]

            val otherUserId = if (chatBean.receiverId != mCurrentUserId) {
                chatBean.receiverId
            } else {
                chatBean.senderId
            }


            FirebaseDatabaseQueries.instance?.getUser(
                otherUserId!!,
                object : FirebaseUserListener {
                    override fun getUser(user: UserBean?) {

                    }
                }, chatBean.jobId
            ) { user: UserBean?, jobId: String? ->
                try {
                    user?.let {
                        val index = messageList.indexOfFirst { it.jobId.equals(jobId) }
                        if (index >= 0) {
                            val chatData = messageList[index]
                            // setting image
                            if (!it.image.isNullOrEmpty()) {
                                chatData.senderImage = it.image!!
                            }

                            if (!it.name.isNullOrEmpty()) {
                                chatData.senderName = it.name
                            }
                            if (it.userType != null) {
                                chatData.senderType = it.userType.toString()
                            }
                            if (!it.image.isNullOrEmpty()) {
                                chatData.senderImage = it.image
                            }
                            messageList?.set(index, chatData)

                            setAdapter()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        Collections.sort(messageList, object : Comparator<ChatMessageBean> {
            override fun compare(
                p0: ChatMessageBean?,
                p1: ChatMessageBean?
            ): Int {
                try {
                    return ((p1?.messageTimestamp as Long).compareTo((p0?.messageTimestamp as Long)))
                } catch (a: Exception) {
                    return 0
                }
            }

        })
        messageList?.forEach {
            Log.d("FM_Data", " message roomId >> ${it?.messageRoomId}")
            Log.d("FM_Data", " message jobId >> ${it?.jobId}")

        }
        Log.d("FM_Data", " message list size >> ${messageList?.size}")
//        messageList=      messageList?.distinctBy { it.jobId } as ArrayList<ChatMessageBean>
        Log.d("FM_Data", "after message list size >> ${messageList?.size}")
        getUnreadMessagesCount(messageList)
        setAdapter()

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {

        }
    }

    public fun manageRecyclerViewVisibility() {
        if (messageList?.isEmpty() == true) {
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
            mBinding.rvMessages.visibility = View.GONE
        } else {
            mBinding.rvMessages.visibility = View.VISIBLE
            mBinding.tvResultTitleNoData.visibility = View.GONE
        }
    }

    public fun manageRecyclerViewVisibility(messageList: ArrayList<ChatMessageBean>) {
        if (messageList?.isEmpty() == true) {
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
            mBinding.rvMessages.visibility = View.GONE
        } else {
            mBinding.rvMessages.visibility = View.VISIBLE
            mBinding.tvResultTitleNoData.visibility = View.GONE
        }
    }

    override fun getMessages(message: ChatMessageBean?) {
        mBinding.swipeMessage.isRefreshing = false
        val index = messageList?.indexOfFirst { it.jobId.equals(message?.jobId) } ?: -1
        if (index >= 0) {

            message?.let { messageList.set(index, it) }
        } else {
            messageList?.add(message!!)
        }
        val mainIndex = mainMessageList?.indexOfFirst { it.jobId.equals(message?.jobId) } ?: -1
        if (mainIndex >= 0) {

            message?.let { mainMessageList.set(mainIndex, it) }
        } else {
            mainMessageList?.add(message!!)
        }
//        mainMessageList?.add(message!!)
//        if (messageList?.size == mainMessageList?.size) {
//            messageList?.forEachIndexed { pos, it ->
//                if (it.jobId == null || it.jobId!!.length == 0) {
//                    messageList?.get(pos)?.jobId = mainMessageList?.get(pos)?.jobId
//                    messageList?.get(pos)?.jobName =
//                        mainMessageList?.get(pos)?.jobName
//                }
//            }
//        }
        getUsersData()
    }

    override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {
        if (messagesList?.isNotEmpty() == true) {
            getLastMessages(messagesList)
        } else {
            mBinding.swipeMessage.isRefreshing = false
            manageRecyclerViewVisibility()
        }
    }

    override fun newMessagesListing() {

        if (mEventCalledFirstTime) {
            //Log.e("is_new_message_arrived","yes")
            mEventCalledFirstTime = false
        } else
            getLastChatMessagesList()
    }

    override fun noData() {
    }

    override fun onPause() {
        super.onPause()
        msgListener?.let {
            msgPath?.removeEventListener(it)
        }
    }


    override fun getCallbackListeners(path: DatabaseReference?, listener: FirebaseEventListeners?) {
        msgPath = path
        msgListener = listener
    }
}