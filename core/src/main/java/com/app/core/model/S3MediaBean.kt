package com.app.core.model.amazons3

import com.google.gson.annotations.SerializedName

data class S3MediaBean(
    val mediaBean: MediaBean? = null,
    val mediaError: MediaError? = null
)
data class ImageUploadResponse (@SerializedName("url")
                              var url: List<String>? = null)
data class ImageUploadURIResponse (@SerializedName("url")
                                var url: List<String>? = null,var tmpURL:String)

data class MediaError(val errMessage: String?)
class MediaBean (
    var name: String? = null,
    var progress: Int = 0,
    var serverUrl: String = "",
    var isSuccess: String = "0",
    var mediaPath: String? = null,
    var mediaId: Int = 0,
    var id: Int = 0)

