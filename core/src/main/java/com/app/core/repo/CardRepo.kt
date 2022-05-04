package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class CardRepo : BaseRepo() {

    suspend fun createClientSecretKey(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.CREATE_CLIENT_SECRET) {
            unauthenticatedApiClient.createClientSecretKey(params)
        }

    suspend fun saveBankTransactionStripe(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.SAVE_STRIPE_BANK_TRANSACTION) {
            unauthenticatedApiClient.saveBankTransactionStripe(params)
        }

    suspend fun getLastUSEDCARD() =
        apiRequest(ApiCodes.GET_LAST_USED_CARD) {
            unauthenticatedApiClient.getLastUSEDCARD()
        }

    suspend fun getAllCards() =
        apiRequest(ApiCodes.CARD_LIST) {
            unauthenticatedApiClient.getAllCards()
        }

    suspend fun addCards(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.ADD_CARD) {
            unauthenticatedApiClient.addCards(params)
        }

    suspend fun pay(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.PAY) {
            unauthenticatedApiClient.pay(params)
        }

    suspend fun updateCards(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.UPDATE_CARD) {
            unauthenticatedApiClient.updateCards(params)
        }

    suspend fun deleteCards(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.DELETE_CARD) {
            unauthenticatedApiClient.deleteCards(params)
        }
}