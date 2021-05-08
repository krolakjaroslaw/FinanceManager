package com.jk.prm.financemanager.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment;")
    fun getAll(): List<PaymentDto>

    @Query("SELECT * FROM payment;")
    fun getAllContentProvider(): Cursor

    @Query("SELECT * FROM payment WHERE id = :id")
    fun getById(id: Long): Cursor

    @Insert
    fun insert(payment: PaymentDto): Long

    @Query("DELETE FROM payment WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("UPDATE payment SET name = :name, category = :category, amount = :amount, date = :date WHERE id = :id")
    fun updateById(id: Long, name: String, category: String, amount: Double, date: String): Int
}