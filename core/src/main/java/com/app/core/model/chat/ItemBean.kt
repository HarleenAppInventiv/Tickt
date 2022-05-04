package com.app.core.model.chat

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Created by appinventiv-pc on 6/10/2021.
 */

class ItemBean() : Parcelable {
    var supplierUserId: String? = null
    var itemTitle: String? = null
    var itemStatus: String? = null
    var itemDescription: String? = null
    var itemId: String? = null
    var itemUrl: String? = null

    constructor(parcel: Parcel) : this() {
        supplierUserId = parcel.readString()
        itemTitle = parcel.readString()
        itemStatus = parcel.readString()
        itemDescription = parcel.readString()
        itemId = parcel.readString()
        itemUrl = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(supplierUserId)
        parcel.writeString(itemTitle)
        parcel.writeString(itemStatus)
        parcel.writeString(itemDescription)
        parcel.writeString(itemId)
        parcel.writeString(itemUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemBean> {
        override fun createFromParcel(parcel: Parcel): ItemBean {
            return ItemBean(parcel)
        }

        override fun newArray(size: Int): Array<ItemBean?> {
            return arrayOfNulls(size)
        }
    }


}