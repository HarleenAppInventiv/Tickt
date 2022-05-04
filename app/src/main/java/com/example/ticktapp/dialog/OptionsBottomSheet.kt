package com.example.ticktapp.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ticktapp.R
import com.example.ticktapp.adapters.OptionsAdapter
import com.example.ticktapp.databinding.OptionsBottomSheetBinding
import com.example.ticktapp.interfaces.RecyclerCallback
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * This bottom sheet is used to show any list of custom options that user wants
 *
 * @property mOptionsList list of options ie [OptionsModel] that user inserted consists of option and option text
 * @property optionsSelectionCallback
 * @constructor
 *
 *
 * @param context
 */
class OptionsBottomSheet(
    context: Context,
    var mOptionsList: ArrayList<OptionsModel>,
    var optionsSelectionCallback: (OptionsModel) -> Unit

) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    private var mOptionsAdapter: OptionsAdapter? = null
    lateinit var mBinding: OptionsBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.options_bottom_sheet,
            null,
            false
        )
        setContentView(mBinding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mBinding.rvProvince.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mOptionsAdapter =
            OptionsAdapter(mOptionsList, object : RecyclerCallback {
                override fun onClick(position: Int, view: View?) {
                    optionsSelectionCallback(mOptionsList.get(position))
                    dismiss()
                }
            })
        mBinding.rvProvince.layoutManager = LinearLayoutManager(context)
        mBinding.rvProvince.adapter = mOptionsAdapter
    }

    fun updateList(optionsList: ArrayList<OptionsModel>) {
        mOptionsAdapter?.notifyDataSetChanged()
    }

    interface OptionsSelectionCallback {
        fun onOptionSelect(optionsModel: OptionsModel?)
    }

    /**
     * Model for each option
     *
     * @property type identify the option type
     * @property message message to show with each option
     */
    class OptionsModel(
        var type: Int? = null,
        var message: String? = null
    )
}