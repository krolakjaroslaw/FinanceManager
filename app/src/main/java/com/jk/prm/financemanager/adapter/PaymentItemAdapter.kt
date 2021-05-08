package com.jk.prm.financemanager.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.RecyclerView
import com.jk.prm.financemanager.*
import com.jk.prm.financemanager.database.PaymentDatabase
import com.jk.prm.financemanager.databinding.PaymentItemBinding
import com.jk.prm.financemanager.model.Payment

class PaymentItemAdapter(
    private val db: PaymentDatabase,
    private var callback: OnClickListener
) : RecyclerView.Adapter<PaymentItemViewHolder>() {
    var items: List<Payment> = emptyList()
    private val main = HandlerCompat.createAsync(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentItemViewHolder {
        return PaymentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::PaymentItemViewHolder)
    }

    override fun onBindViewHolder(holder: PaymentItemViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)

        holder.itemView.setOnClickListener {
            callback.onClick(it.context, item)
        }

        holder.itemView.setOnLongClickListener {
            val dialog = AlertDialog.Builder(it.context)
                .setTitle("Delete ${item.name}")
                .setMessage("Do you really want to remove ${item.name}?")
                .setCancelable(false)
                .setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("YES") { dialog, _ ->
                    callback.onLongClick(it.context, holder)
                    dialog.dismiss()
                }
            dialog.show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = items.size

    fun load() {
        items = db.payments.getAll()
            .sortedByDescending { it.date }
            .map { it.toModel() }
        main.post {
            notifyDataSetChanged()
        }
    }

    fun deleteItem(position: Int) {
        val item = items[position]
        db.payments.deleteById(item.id)
    }

    interface OnClickListener {
        fun onClick(context: Context, item: Payment)
        fun onLongClick(context: Context, holder: PaymentItemViewHolder)
    }
}