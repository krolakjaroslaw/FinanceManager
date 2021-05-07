package com.jk.prm.financemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.jk.prm.financemanager.database.PaymentDao
import com.jk.prm.financemanager.database.PaymentDatabase
import com.jk.prm.financemanager.database.PaymentDto

const val PROVIDER_AUTHORITY = "com.jk.prm.financemanager.provider"
const val PAYMENT_TABLE = "payment"

private lateinit var database: PaymentDatabase
private var paymentDao: PaymentDao? = null

class PaymentContentProvider : ContentProvider() {

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(PROVIDER_AUTHORITY, PAYMENT_TABLE, 1)
        addURI(PROVIDER_AUTHORITY, "$PAYMENT_TABLE/#", 2)
    }

    override fun onCreate(): Boolean {
        database = PaymentDatabase.open(context!!)
        paymentDao = database.payments
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code: Int = sUriMatcher.match(uri)
        return if (code == 1 || code == 2) {
            val context = context ?: return null
            val cursor: Cursor? = if (code == 1) {
                paymentDao?.getAllContentProvider()
            } else {
                paymentDao?.getById(ContentUris.parseId(uri))
            }
            cursor?.setNotificationUri(context.contentResolver, uri)
            cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (sUriMatcher.match(uri)) {
            1 -> "vnd.android.cursor.dir/$PROVIDER_AUTHORITY.$PAYMENT_TABLE"
            2 -> "vnd.android.cursor.item/$PROVIDER_AUTHORITY.$PAYMENT_TABLE"
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (sUriMatcher.match(uri)) {
            1 -> {
                val context = context ?: return null
                val id: Long? = paymentDao?.insert(PaymentDto.fromContentValues(values))
                context.contentResolver.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, id!!)
            }
            2 -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (sUriMatcher.match(uri)) {
            1 -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            2 -> {
                val context = context ?: return 0
                val count: Int = paymentDao!!.deleteById(ContentUris.parseId(uri))
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return when (sUriMatcher.match(uri)) {
            1 -> throw IllegalArgumentException(
                "Invalid URI, cannot update without ID$uri"
            )
            2 -> {
                val context = context ?: return 0
                val payment: PaymentDto = PaymentDto.fromContentValues(values)
                val count: Int = paymentDao!!.updateById(
                    id = ContentUris.parseId(uri),
                    name = payment.name,
                    category = payment.category,
                    amount = payment.amount,
                    date = payment.date
                )
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}