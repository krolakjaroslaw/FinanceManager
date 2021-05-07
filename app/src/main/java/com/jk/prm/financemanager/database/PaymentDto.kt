package com.jk.prm.financemanager.database

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jk.prm.financemanager.model.Payment

private const val COLUMN_ID = "id"
private const val COLUMN_NAME = "name"
private const val COLUMN_CATEGORY = "category"
private const val COLUMN_AMOUNT = "amount"
private const val COLUMN_DATE = "date"

@Entity(tableName = "Payment")
class PaymentDto(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String = "",
    // TODO: not working
    var category: String = "<no category>",
//    var amount: String = "",
    var amount: Double = 0.0,
    var date: String = ""
) {

    fun toModel() = Payment(
        id,
        name,
        category,
//        Amount(amount.toDouble()),
        amount,
        date
    )

    companion object {
        fun fromContentValues(values: ContentValues?): PaymentDto {
            val payment = PaymentDto()
            if (values != null && values.containsKey(COLUMN_ID)) {
                payment.id = values.getAsLong(COLUMN_ID)
            }
            if (values != null && values.containsKey(COLUMN_NAME)) {
                payment.name = values.getAsString(COLUMN_NAME)
            }
            if (values != null && values.containsKey(COLUMN_CATEGORY)) {
                payment.category = values.getAsString(COLUMN_CATEGORY)
            }
            if (values != null && values.containsKey(COLUMN_AMOUNT)) {
//                payment.amount = values.getAsString(COLUMN_AMOUNT)
                payment.amount = values.getAsDouble(COLUMN_AMOUNT)
            }
            if (values != null && values.containsKey(COLUMN_DATE)) {
                payment.date = values.getAsString(COLUMN_DATE)
            }
            return payment
        }
    }
}