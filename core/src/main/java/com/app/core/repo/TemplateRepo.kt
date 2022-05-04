package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class TemplateRepo : BaseRepo() {
    suspend fun createTemplate(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.CREATE_TEMPLATE) {
            unauthenticatedApiClient.createTemplate(data)
        }

    suspend fun getTemplateList() =
        apiRequest(ApiCodes.TEMPLATE_LIST) {
            unauthenticatedApiClient.getTemplateList()
        }
    suspend fun getTemplateMilestoneList(tempID:String) =
        apiRequest(ApiCodes.TEMPLATE_MILESTONE_LIST) {
            unauthenticatedApiClient.getTemplateMilestoneList(tempID)
        }
    suspend fun deleteTemplateMilestoneList(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.TEMPLATE_DELETE_MILESTONE_LIST) {
            unauthenticatedApiClient.deleteTemplateMilestoneList(data)
        }
}