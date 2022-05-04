package com.example.ticktapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.NotificationModel
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemNotificationsBinding
import com.example.ticktapp.util.DateUtils

class NotificationsAdapter(
    var notificaitons: ArrayList<NotificationModel>,
    val listner: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_notifications, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return notificaitons.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                notificaitons[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: RowitemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: NotificationModel) {
            binding.tvTitle.text = jobs.title
            binding.tvDetails.text = jobs.notificationText
            val diffDate = DateUtils.calculateTimeBetweenTwoDates(
                DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMAT_18,
                    DateUtils.DATE_FORMATE_4,
                    jobs.updatedAt.toString()
                ),
                DateUtils.getCurrentDateTime(DateUtils.DATE_FORMATE_4), false
            )
            binding.tvTimes.text = diffDate
            try {
                Glide.with(binding.root.context).load(jobs.image)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            }
            catch (e:Exception){
                Log.i("notificationImage", "bind: $e")
            }
            if (jobs.read != null && jobs.read == 0) {
                binding.viewMsgRead.visibility = View.VISIBLE
            } else {
                binding.viewMsgRead.visibility = View.GONE
            }
            binding.llMain.tag = absoluteAdapterPosition
            binding.llMain.setOnClickListener(listner)
        }
    }

    fun setData(data: ArrayList<NotificationModel>) {
        this.notificaitons.clear()
        this.notificaitons.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: ArrayList<NotificationModel>) {
        this.notificaitons.addAll(data)
        notifyDataSetChanged()
    }

    fun getData(): List<NotificationModel> {
        return this.notificaitons

    }

    fun updateNotificationStatus(pos: Int) {
        this.notificaitons.get(pos).read = 1
        notifyItemChanged(pos)
    }
}
