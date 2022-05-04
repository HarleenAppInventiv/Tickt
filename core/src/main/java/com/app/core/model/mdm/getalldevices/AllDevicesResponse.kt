package com.app.core.model.mdm.getalldevices

data class AllDevicesResponse(
    val count: Int?,
    val next: String?,
    val previous: String,
    val results: ArrayList<DeviceInfo>?
)

data class DeviceInfo(val id: Int?, val serial_number: String?, val enrollment_status: String?)