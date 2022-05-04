package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradie.VouchesData
import com.app.core.repo.VouchRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class VoucherViewModel : BaseViewModel() {

    private val mRepo by lazy { VouchRepo() }
    lateinit var vouchData: VouchesData

    /**
     * Api call to add voucher.
     */
    fun addVoucher(data: HashMap<String, Any>) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addVoucher(data)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), VouchesData::class.java)
                        vouchData = responseModel

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
}