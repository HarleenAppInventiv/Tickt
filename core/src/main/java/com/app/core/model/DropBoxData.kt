package com.app.core.model

import com.google.gson.annotations.SerializedName

data class DropBoxData(
    @SerializedName("path")
    var path: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("folder_name")
    var folderName: String? = null,
    @SerializedName("file_name")
    var fileName: String? = null,
    @SerializedName("file_type")
    var fileType: Int? = null,
    @SerializedName("multiStri")
    var multiStri: String? = null,
)