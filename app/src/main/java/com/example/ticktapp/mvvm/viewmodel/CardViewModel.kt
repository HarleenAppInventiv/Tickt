package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.BankPaymentStripe.ClientSecretResponse
import com.app.core.model.cards.CreditCard
import com.app.core.repo.CardRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class CardViewModel : BaseViewModel() {

    private val mRepo by lazy { CardRepo() }
    public lateinit var creditCard: CreditCard
    public lateinit var clientSecretResponse: ClientSecretResponse
    public lateinit var creditCards: List<CreditCard>

    fun createClientSecretKey(param: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.createClientSecretKey(param)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            Log.d("clientSecretResponse", it.data.toString())
                            val responseModel =
                                Gson().fromJson(
                                    Gson().toJson(it.data),
                                    ClientSecretResponse::class.java
                                )
                            clientSecretResponse = responseModel
                        } catch (e: Exception) {
                            Log.d("clientSecretResponse", e.message.toString())
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun saveBankTransactionStripe(param: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.saveBankTransactionStripe(param)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val responseModel =
                                Gson().fromJson(
                                    Gson().toJson(it.data),
                                    ClientSecretResponse::class.java
                                )
                            clientSecretResponse = responseModel
                        } catch (e: Exception) {
                            Log.d("credit_card", e.message.toString())
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun getLastUSEDCARD() {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getLastUSEDCARD()
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val responseModel =
                                Gson().fromJson(Gson().toJson(it.data), CreditCard::class.java)
                            creditCard = responseModel
                        } catch (e: Exception) {
                            Log.d("credit_card", e.message.toString())
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun addCards(param: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addCards(param)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val responseModel =
                                Gson().fromJson(Gson().toJson(it.data), CreditCard::class.java)
                            creditCard = responseModel
                        } catch (e: Exception) {
                            Log.d("credit_card", e.message.toString())
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun pay(param: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.pay(param)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {

                        } catch (e: Exception) {
                            Log.d("credit_card", e.message.toString())
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun updateCards(param: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.updateCards(param)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val responseModel =
                                Gson().fromJson(Gson().toJson(it.data), CreditCard::class.java)
                            creditCard = responseModel
                        } catch (e: Exception) {
                            Log.d("credit_card", e.message.toString())
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun deleteCards(param: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.deleteCards(param)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun getAllCards() {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getAllCards()
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val gson = GsonBuilder().create()
                            val model =
                                gson.fromJson(Gson().toJson(it.data), Array<CreditCard>::class.java)
                                    .toList()
                            creditCards = ArrayList(model)
                        } catch (e: Exception) {
                            Log.d("credit_card", e.message.toString())
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}



