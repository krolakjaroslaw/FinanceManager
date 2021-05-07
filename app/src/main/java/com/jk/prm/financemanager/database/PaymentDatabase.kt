package com.jk.prm.financemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val FILENAME = "payment"

@Database(
    entities = [PaymentDto::class],
    version = 1
)
abstract class PaymentDatabase : RoomDatabase() {
    abstract val payments: PaymentDao

    companion object {
        fun open(context: Context) = Room.databaseBuilder(
            context, PaymentDatabase::class.java, FILENAME
        ).build()
    }
}