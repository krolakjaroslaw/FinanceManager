package com.jk.prm.financemanager.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class Payment(
    val id: Long,
    val name: String?,
    val category: String?,
    val amount: Double,
    val date: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeDouble(amount)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Payment> {
        override fun createFromParcel(parcel: Parcel): Payment {
            return Payment(parcel)
        }

        override fun newArray(size: Int): Array<Payment?> {
            return arrayOfNulls(size)
        }
    }
}

//class Amount(var value: Double) {
//
//    override fun toString(): String {
//        return String.format("%.2f", value) + " PLN"
//    }
//}