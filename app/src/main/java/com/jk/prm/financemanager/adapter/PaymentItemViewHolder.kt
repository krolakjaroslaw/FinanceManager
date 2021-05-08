package com.jk.prm.financemanager.adapter

import androidx.recyclerview.widget.RecyclerView
import com.jk.prm.financemanager.R
import com.jk.prm.financemanager.databinding.PaymentItemBinding
import com.jk.prm.financemanager.model.Payment
import java.text.SimpleDateFormat
import java.util.*

class PaymentItemViewHolder(
    private val paymentItemBinding: PaymentItemBinding
) : RecyclerView.ViewHolder(paymentItemBinding.root) {

    fun bindItem(payment: Payment) {
        with(paymentItemBinding) {
            val amount = String.format("%.2f", payment.amount) + " zł"
            if (payment.amount > 0) itemImage.setImageResource(R.drawable.payment_in)
            else itemImage.setImageResource(R.drawable.payment_out)

            itemDate.text = convertToDate(payment.date!!)
            itemName.text = payment.name
            itemCategory.text = payment.category
            itemAmount.text = amount
        }
    }

    private fun convertToDate(toConvert: String): String {
        val dateParse = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateDisplay = SimpleDateFormat("d MMM yyyy", Locale.US)
        return dateDisplay.format(dateParse.parse(toConvert)!!)
    }
}