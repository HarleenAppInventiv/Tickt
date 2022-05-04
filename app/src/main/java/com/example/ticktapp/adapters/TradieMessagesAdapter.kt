package com.example.ticktapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.chat.ChatMessageBean
import com.app.core.preferences.PreferenceManager
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.mvvm.view.tradie.ChatTradieActivity
import com.example.ticktapp.mvvm.view.tradie.TradieMessageFragment
import com.example.ticktapp.util.DateUtils
import kotlinx.android.synthetic.main.row_chat_list.view.*
import java.util.*


class TradieMessagesAdapter(
    private var messageTmpList: ArrayList<ChatMessageBean>
) : RecyclerView.Adapter<TradieMessagesAdapter.MessageViewHolder>() {
    var isSupporterNotes: Boolean = false
    lateinit var tradieMessageFragment: TradieMessageFragment
    lateinit var context: Context
    private var loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
    var messageList: ArrayList<ChatMessageBean>
    fun setForSupporterNotes(value: Boolean) {
        isSupporterNotes = value
    }

    private var mUserId: String? = null

    init {
        messageList = messageTmpList
        mUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        context = parent.context
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_chat_list, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.onBind(messageList[position])
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(data: ChatMessageBean?) {
            data?.apply {
                val dateToSet = (messageTimestamp as Long)
                val date = Date()
                date.time = dateToSet
                val diffDate=DateUtils.calculateTimeBetweenTwoDates(DateUtils.getCurrentDateTime(date, DateUtils.DATE_FORMATE_4),DateUtils.getCurrentDateTime( DateUtils.DATE_FORMATE_4),false)
                itemView.tv_time.text = diffDate
                itemView.tv_status.text = data.senderName
                Glide.with(context)
                    .load(data.senderImage)
                    .placeholder(R.drawable.placeholder_profile)
                    .error(R.drawable.placeholder_profile)
                    .into(itemView.iv_image)
                itemView.tv_title.text = data.jobName
                // set message
                itemView.tv_message.text =
                    if (messageText?.isNotEmpty() == true) (messageText) else "Image"

                // set unread count
                if (unreadMessages != 0L) {
                    itemView.tv_unread_message_count.text = unreadMessages.toString()
                    itemView.tv_unread_message_count.visibility = View.VISIBLE
                } else {
                    itemView.tv_unread_message_count.visibility = View.GONE
                }
                itemView.setOnClickListener {
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            ChatTradieActivity::class.java
                        ).putExtra("data", data)
                    //Intent(
                        //                            itemView.context,
                        //                            ChatTradieActivity::class.java
                        //                        ).putExtra("data", data)
                    )
                }

            }
        }
    }


    private var fRecords: Filter? = null

    @Override
    fun getFilter(): Filter? {
        if (fRecords == null) {
            fRecords = RecordFilter()
        }
        return fRecords
    }

    fun setContext(tradieMessageFragment: TradieMessageFragment) {
        this.tradieMessageFragment = tradieMessageFragment
    }

    inner class RecordFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()

            if (constraint.isEmpty()) {
                results.values = messageTmpList
                results.count = messageTmpList.size
            } else {
                val fRecords = ArrayList<ChatMessageBean>()
                for (s in messageTmpList) {
                    if (s.senderName?.lowercase()
                            ?.contains(constraint.toString().lowercase()) == true
                    ) {
                        fRecords.add(s)
                    }
                }
                results.values = fRecords
                results.count = fRecords.size
            }
            return results
        }

        @Override
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            messageList = results.values as ArrayList<ChatMessageBean>
            tradieMessageFragment.manageRecyclerViewVisibility(messageList)
            notifyDataSetChanged()
        }
    }
}