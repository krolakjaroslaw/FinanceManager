package com.jk.prm.financemanager.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jk.prm.financemanager.databinding.PaymentItemBinding
import com.jk.prm.financemanager.model.Payment

class PaymentItemAdapter(
    private var items: MutableList<Payment>,
    private var callback: OnClickListener
) : RecyclerView.Adapter<PaymentItemViewHolder>() {

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
                    callback.onLongClick(it.context, item)
                    dialog.dismiss()
                }
            dialog.show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = items.size

    interface OnClickListener {
        fun onClick(context: Context, item: Payment)
        fun onLongClick(context: Context, item: Payment)
    }
}