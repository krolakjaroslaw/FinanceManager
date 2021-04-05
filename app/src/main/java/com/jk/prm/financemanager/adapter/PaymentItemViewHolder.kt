package com.jk.prm.financemanager.adapter

import androidx.recyclerview.widget.RecyclerView
import com.jk.prm.financemanager.R
import com.jk.prm.financemanager.databinding.PaymentItemBinding
import com.jk.prm.financemanager.model.Payment

class PaymentItemViewHolder(
    private val paymentItemBinding: PaymentItemBinding
) : RecyclerView.ViewHolder(paymentItemBinding.root) {

    fun bindItem(payment: Payment) {
        if (payment.amount.value > 0)
            paymentItemBinding.itemImage.setImageResource(R.drawable.payment_in)

        paymentItemBinding.itemDate.text = payment.date
        paymentItemBinding.itemName.text = payment.name
        paymentItemBinding.itemCategory.text = payment.category
        paymentItemBinding.itemAmount.text = payment.amount.toString()
    }
}