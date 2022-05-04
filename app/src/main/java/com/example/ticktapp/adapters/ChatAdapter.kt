package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.chat.ChatMessageBean
import com.app.core.preferences.PreferenceManager
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.firebase.FirebaseConstants
import com.example.ticktapp.mvvm.view.DialogImageViewPostActivity
import com.example.ticktapp.mvvm.view.VideoOpenActivity
import com.example.ticktapp.util.DateUtils
import kotlinx.android.synthetic.main.row_support_chat.view.*
import java.util.*


class ChatAdapter(
    val messageList: ArrayList<ChatMessageBean>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    val userId = PreferenceManager.getString(PreferenceManager.USER_ID)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_support_chat, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.onBind(messageList[position])
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(data: ChatMessageBean?) {
            val today = Calendar.getInstance()
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DATE, 1)
            data?.apply {
                itemView.tv_date.visibility = View.GONE
                if (messageList.size == 1) {
                    itemView.tv_date.visibility = View.VISIBLE
                    if (DateUtils.getDateFromTimeStamp(today.timeInMillis, DateUtils.DATE_FORMATE_8)
                            .toString()
                            .equals(
                                DateUtils.getDateFromTimeStamp(
                                    messageTimestamp as Long?,
                                    DateUtils.DATE_FORMATE_8
                                ).toString()
                            )
                    ) {
                        itemView.tv_date.text = itemView.context.getString(R.string.today)
                    } else if (DateUtils.getDateFromTimeStamp(
                            tomorrow.timeInMillis,
                            DateUtils.DATE_FORMATE_8
                        ).toString()
                            .equals(
                                DateUtils.getDateFromTimeStamp(
                                    messageTimestamp as Long?,
                                    DateUtils.DATE_FORMATE_8
                                ).toString()
                            )
                    ) {
                        itemView.tv_date.text = itemView.context.getString(R.string.yesterday)
                    } else {
                        itemView.tv_date.text =
                            DateUtils.getDateFromTimeStamp(
                                messageTimestamp as Long?,
                                DateUtils.DATE_FORMATE_8
                            ).toString()
                    }

                } else {
                    if (absoluteAdapterPosition != 0 && !DateUtils.getDateFromTimeStamp(
                            messageList.get(absoluteAdapterPosition).messageTimestamp as Long?,
                            DateUtils.DATE_FORMATE_8
                        ).toString().equals(
                            DateUtils.getDateFromTimeStamp(
                                messageList.get(absoluteAdapterPosition - 1).messageTimestamp as Long?,
                                DateUtils.DATE_FORMATE_8
                            ).toString()
                        )
                    ) {
                        itemView.tv_date.visibility = View.VISIBLE
                        if (DateUtils.getDateFromTimeStamp(
                                today.timeInMillis,
                                DateUtils.DATE_FORMATE_8
                            ).toString()
                                .equals(
                                    DateUtils.getDateFromTimeStamp(
                                        messageTimestamp as Long?,
                                        DateUtils.DATE_FORMATE_8
                                    ).toString()
                                )
                        ) {
                            itemView.tv_date.text = itemView.context.getString(R.string.today)
                        } else if (DateUtils.getDateFromTimeStamp(
                                tomorrow.timeInMillis,
                                DateUtils.DATE_FORMATE_8
                            ).toString()
                                .equals(
                                    DateUtils.getDateFromTimeStamp(
                                        messageTimestamp as Long?,
                                        DateUtils.DATE_FORMATE_8
                                    ).toString()
                                )
                        ) {
                            itemView.tv_date.text = itemView.context.getString(R.string.yesterday)
                        } else {
                            itemView.tv_date.text =
                                DateUtils.getDateFromTimeStamp(
                                    messageTimestamp as Long?,
                                    DateUtils.DATE_FORMATE_8
                                ).toString()
                        }
                    } else if (absoluteAdapterPosition == 0) {
                        itemView.tv_date.visibility = View.VISIBLE
                        if (DateUtils.getDateFromTimeStamp(
                                today.timeInMillis,
                                DateUtils.DATE_FORMATE_8
                            ).toString()
                                .equals(
                                    DateUtils.getDateFromTimeStamp(
                                        messageTimestamp as Long?,
                                        DateUtils.DATE_FORMATE_8
                                    ).toString()
                                )
                        ) {
                            itemView.tv_date.text = itemView.context.getString(R.string.today)
                        } else if (DateUtils.getDateFromTimeStamp(
                                tomorrow.timeInMillis,
                                DateUtils.DATE_FORMATE_8
                            ).toString()
                                .equals(
                                    DateUtils.getDateFromTimeStamp(
                                        messageTimestamp as Long?,
                                        DateUtils.DATE_FORMATE_8
                                    ).toString()
                                )
                        ) {
                            itemView.tv_date.text = itemView.context.getString(R.string.yesterday)
                        } else {
                            itemView.tv_date.text =
                                DateUtils.getDateFromTimeStamp(
                                    messageTimestamp as Long?,
                                    DateUtils.DATE_FORMATE_8
                                ).toString()
                        }
                    }
                }

                itemView.clReceiver.visibility = View.GONE
                itemView.clSender.visibility = View.GONE

                val userType = if (senderId == userId) {
                    "RECEIVER"
                } else {
                    "SENDER"
                }
                if (userType == "RECEIVER") {
                    itemView.clReceiver.visibility = View.VISIBLE

                    // setTime
                    if (!messageTimestamp.toString().contains("sv=timestamp")) {
                        itemView.tv_receiver_date.text =
                            DateUtils.getDateFromTimeStamp(
                                messageTimestamp.toString().toLong(),
                                "hh:mm a"
                            )
                    } else itemView.tv_receiver_date.text = "Just Now"

                    // setting message
                    itemView.tv_receiver_message.visibility = View.GONE
                    itemView.ivReceiverImage.visibility = View.GONE

                    if (messageType == FirebaseConstants.TEXT) {
                        itemView.tv_receiver_message.visibility = View.VISIBLE
                        itemView.tv_receiver_message.text = messageText
                    } else {
                        itemView.ivReceiverImage.visibility = View.VISIBLE
                        if (messageType == FirebaseConstants.VIDEO) {
                            itemView.ivReceiverImage.setPadding(130, 130, 130, 130)
                            itemView.ivReceiverImage.setImageResource(R.drawable.ic_play_svg)
                        } else {
                            itemView.ivReceiverImage.setPadding(0, 0, 0, 0)
                            mediaUrl?.let {
                                Glide.with(itemView.ivReceiverImage.context).load(mediaUrl!!)
                                    .placeholder(R.drawable.placeholder_profile)
                                    .into(itemView.ivReceiverImage)
                            }
                        }
                    }

                } else {
                    itemView.clSender.visibility = View.VISIBLE

                    if (!messageTimestamp.toString().contains("sv=timestamp")) {
                        itemView.tv_sender_date.text =
                            DateUtils.getDateFromTimeStamp(
                                messageTimestamp.toString().toLong(),
                                "hh:mm a"
                            )
                    } else itemView.tv_receiver_date.text = "Just Now"

                    // setting message
                    itemView.tv_sender_message.visibility = View.GONE
                    itemView.ivSenderImage.visibility = View.GONE

                    if (messageType == FirebaseConstants.TEXT) {
                        itemView.tv_sender_message.visibility = View.VISIBLE
                        itemView.tv_sender_message.text = messageText
                    } else {
                        itemView.ivSenderImage.visibility = View.VISIBLE
                        if (messageType == FirebaseConstants.VIDEO) {
                            itemView.ivSenderImage.setPadding(130, 130, 130, 130)
                            itemView.ivSenderImage.setImageResource(R.drawable.ic_play_svg)
                        } else {
                            itemView.ivSenderImage.setPadding(0, 0, 0, 0)
                            mediaUrl?.let {
                                Glide.with(itemView.ivSenderImage.context).load(mediaUrl!!)
                                    .placeholder(R.drawable.placeholder_profile)
                                    .into(itemView.ivSenderImage)
                            }
                        }
                    }
                }
                itemView.ivSenderImage.setOnClickListener {
                    if (mediaUrl?.lowercase()?.endsWith(".png") == true || mediaUrl?.lowercase()
                            ?.endsWith(".jpeg") == true || mediaUrl?.lowercase()
                            ?.endsWith("jpg") == true
                    ) {
                        val images = ArrayList<String>()
                        mediaUrl?.let { it1 -> images.add(it1) }
                        it.context.startActivity(
                            Intent(it.context, DialogImageViewPostActivity::class.java)
                                .putExtra("photos", images)
                                .putExtra("pos", position)
                        )
                    } else if (mediaUrl?.lowercase()?.endsWith(".mp4") == true) {
                        it.context.startActivity(
                            Intent(it.context, VideoOpenActivity::class.java)
                                .putExtra("data", mediaUrl)
                        )
                    } else {
                        it.context.startActivity(
                            Intent(it.context, VideoOpenActivity::class.java)
                                .putExtra("data", mediaUrl)
                        )
                    }
                }
                itemView.ivReceiverImage.setOnClickListener {
                    if (mediaUrl?.lowercase()?.endsWith(".png") == true || mediaUrl?.lowercase()
                            ?.endsWith(".jpeg") == true || mediaUrl?.lowercase()
                            ?.endsWith("jpg") == true
                    ) {
                        val images = ArrayList<String>()
                        mediaUrl?.let { it1 -> images.add(it1) }
                        it.context.startActivity(
                            Intent(it.context, DialogImageViewPostActivity::class.java)
                                .putExtra("photos", images)
                                .putExtra("pos", 0)
                        )
                    } else if (mediaUrl?.lowercase()?.endsWith(".mp4") == true) {
                        it.context.startActivity(
                            Intent(it.context, VideoOpenActivity::class.java)
                                .putExtra("data", mediaUrl)
                        )
                    } else {
                        it.context.startActivity(
                            Intent(it.context, VideoOpenActivity::class.java)
                                .putExtra("data", mediaUrl)
                        )
                    }
                }
            }
        }
    }
}
