package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradie.VouchesData
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowVounchesBinding
import com.example.ticktapp.mvvm.view.DialogImageViewPostActivity
import com.example.ticktapp.mvvm.view.FileOpenActivity

class VounchesAdapter(var reviewData: ArrayList<VouchesData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_vounches, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        if (reviewData.size > 2)
            return 2
        return reviewData.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                reviewData[position]?.let { holder.bind(it) }

            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowVounchesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vouchesData: VouchesData) {
            binding.tvUserName.text = vouchesData.builderName
            binding.tvDate.text = vouchesData.date
            binding.tvVounchesName.text =
                binding.root.context.getString(R.string.vouch_for) + " " + vouchesData.tradieName
            binding.tvJobName.text = vouchesData.jobName
            binding.tvDesc.text = vouchesData.vouchDescription
            Glide.with(binding.root.context).load(vouchesData.builderImage)
                .placeholder(R.drawable.placeholder_profile)
                .into(binding.ivUserProfile)
            binding.tvVounchesName.setOnClickListener {
                if (vouchesData.recommendation != null && vouchesData.recommendation!!.length > 0) {
                    if (vouchesData.recommendation!!.lowercase()
                            .endsWith(".png") || vouchesData.recommendation!!.lowercase()
                            .endsWith(".jpeg") || vouchesData.recommendation!!.lowercase()
                            .endsWith("jpg")
                    ) {
                        val images = ArrayList<String>()
                        images.add(vouchesData.recommendation!!)
                        it.context.startActivity(
                            Intent(it.context, DialogImageViewPostActivity::class.java)
                                .putExtra("photos", images)
                                .putExtra("pos", 0)
                        )
                    } else {
                        binding.tvVounchesName.context.startActivity(
                            Intent(
                                binding.tvVounchesName.context,
                                FileOpenActivity::class.java
                            ).putExtra("data", vouchesData.recommendation)
                        )
                    }
                } else if (vouchesData.photos != null && vouchesData.photos!!.size > 0) {
                    if (vouchesData.photos!!.get(0).lowercase()
                            .endsWith(".png") || vouchesData.photos!!.get(0).lowercase()
                            .endsWith(".jpeg") || vouchesData.photos!!.get(0).lowercase()
                            .endsWith("jpg")
                    ) {
                        val images = ArrayList<String>()
                        images.add(vouchesData.photos!!.get(0))
                        it.context.startActivity(
                            Intent(it.context, DialogImageViewPostActivity::class.java)
                                .putExtra("photos", images)
                                .putExtra("pos", 0)
                        )
                    } else {
                        binding.tvVounchesName.context.startActivity(
                            Intent(
                                binding.tvVounchesName.context,
                                FileOpenActivity::class.java
                            ).putExtra("data", vouchesData.photos!!.get(0))
                        )
                    }
                }
            }
        }
    }

}