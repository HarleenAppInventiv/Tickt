package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.Qualification
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemDocumentsBinding

class AddDocumentAdapter(
    private val qualList: ArrayList<Qualification>,
    private val listener: DocListAdapterListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_documents, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return qualList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                holder.bind(qualList[position])
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemDocumentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(qualification: Qualification) {
            binding.tvTrade.text = qualification.name
            if (qualification.isSelected == true) {
                if (qualification.isUploaded == true) {
                    binding.tvFile.alpha = 1f
                    binding.ivCancel.visibility = View.VISIBLE
                    binding.tvFile.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorPrimary
                        )
                    )
                    if (!qualification.serverUrl.isNullOrBlank() && qualification.fileExt.equals(".png") ||
                        qualification.serverUrl!!.contains(".jpeg")
                    ) {
                        binding.tvFile.setText("Image")
                        binding.tvFile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.png_1, 0, 0, 0)

                    }
                    else if (!qualification.serverUrl.isNullOrBlank() && qualification.serverUrl!!.contains(".pdf")) {
                        binding.tvFile.setText("Pdf")
                        binding.tvFile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pdf, 0, 0, 0)
                    }

                    else if (!qualification.serverUrl.isNullOrBlank() && qualification.serverUrl!!.contains(".doc") ||
                        qualification.serverUrl!!.contains(".docx")  ) {
                        binding.tvFile.setText("Doc")
                        binding.tvFile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.doc, 0, 0, 0)
                    }
                    binding.tvFile.background =
                        ContextCompat.getDrawable(binding.root.context, R.drawable.bg_selected_border)
                } else {
                    binding.tvFile.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.color_123f95
                        )
                    )
                    binding.tvFile.setText(R.string.upload)
 /*                   binding.tvFile.background =
                        ContextCompat.getDrawable(binding.root.context, R.drawable.bg_selected_rect)
                    binding.tvFile.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.white
                        )
                    )

  */
                    binding.tvFile.background=ContextCompat.getDrawable(binding.tvFile.context,R.drawable.bg_round_corner_dfe5ef)
                    binding.tvFile.alpha = 1f
                    binding.tvFile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    binding.ivCancel.visibility = View.GONE
                }
                binding.cbQualification.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.ic_check)

            } else {
                binding.tvFile.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_123f95
                    )
                )
                binding.cbQualification.background =
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_checkbox_un_active
                    )

   /*             binding.tvFile.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.bg_light_rect)
                binding.tvFile.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorPrimary
                    )
                )

    */          binding.tvFile.background=ContextCompat.getDrawable(binding.tvFile.context,R.drawable.bg_round_corner_dfe5ef)
                binding.tvFile.alpha = 0.4f
                binding.tvFile.setText(R.string.upload)
                binding.tvFile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding.ivCancel.visibility = View.GONE

            }


            itemView.setOnClickListener {
                listener.onDocSelected(adapterPosition, binding)
            }

            binding.ivCancel.setOnClickListener {
                listener.onDocCanceled(adapterPosition, binding)
            }
            binding.tvFile.setOnClickListener {
                listener.onDocUpload(adapterPosition)
            }
        }
    }


    interface DocListAdapterListener {
        fun onDocUpload(position: Int)
        fun onDocSelected(position: Int, binding: RowitemDocumentsBinding)
        fun onDocCanceled(position: Int, binding: RowitemDocumentsBinding)

    }


}
