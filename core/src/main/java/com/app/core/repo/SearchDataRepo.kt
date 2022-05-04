package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class SearchDataRepo : BaseRepo() {
    suspend fun search(searchText: String) =
        apiRequest(ApiCodes.SEARCH_DATA) {
            unauthenticatedApiClient.getSearchData(searchText)
        }

    suspend fun getRecentSearchData() =
        apiRequest(ApiCodes.RECENT_SEARCH) {
            unauthenticatedApiClient.getRecentSearchData()
        }
}