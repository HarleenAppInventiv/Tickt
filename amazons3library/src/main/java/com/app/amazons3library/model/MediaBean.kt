package com.app.amazons3library.model

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver

class MediaBean (
        var name: String? = null,
        var progress: Int = 0,
        var mObserver: TransferObserver? = null,
        var serverUrl: String = "",
        var isSuccess: String = "0",
        var mediaPath: String? = null,
        var mediaId: Int = 0,
        var id: Int = 0)
