package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.model.builderreview.BuilderReviewModel
import com.app.core.util.ApiCodes

class BuilderRepo: BaseRepo() {
    suspend fun postRating(rating:BuilderReviewModel) =
        apiRequest(ApiCodes.BUILDER_REVIEW) {
            unauthenticatedApiClient.postBuilderReview(rating)
        }
}