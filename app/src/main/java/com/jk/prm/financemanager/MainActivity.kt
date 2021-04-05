package com.jk.prm.financemanager

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jk.prm.financemanager.adapter.PaymentItemAdapter
import com.jk.prm.financemanager.databinding.ActivityMainBinding
import com.jk.prm.financemanager.model.Amount
import com.jk.prm.financemanager.model.Payment

class MainActivity : AppCompatActivity(), PaymentItemAdapter.OnClickListener {
    private val layoutBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var mSummary: Amount = Amount(0.0)
    var mItemList: MutableList<Payment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        setupPaymentList()

        layoutBinding.addItemBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(it.context)
                .setTitle("Add")
                .setMessage("adding")
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupPaymentList() = with(layoutBinding.paymentList) {
        layoutManager = LinearLayoutManager(this@MainActivity)
//        this.addItemDecoration(
//                DividerItemDecoration(this@MainActivity, LinearLayoutManager.HORIZONTAL)
//        )
        mItemList = mutableListOf(
            Payment("1 kwi", "Biedronka", "jedzenie", Amount(-100.00)),
            Payment("2 kwi", "Multikino", "rozrywka", Amount(-50.00)),
            Payment("3 kwi", "Medicover", "zdrowie", Amount(-150.00)),
            Payment("4 kwi", "PJATK", "edukacja", Amount(-800.00)),
            Payment("5 kwi", "Orange", "opłaty", Amount(-50.00)),
            Payment("6 kwi", "Praca", "wynagrodzenie", Amount(7000.00)),
            Payment("7 kwi", "Biedronka", "jedzenie", Amount(-100.00)),
            Payment("8 kwi", "Multikino", "rozrywka", Amount(-50.00)),
            Payment("9 kwi", "Medicover", "zdrowie", Amount(-150.00)),
            Payment("10 kwi", "PJATK", "edukacja", Amount(-800.00)),
            Payment("11 kwi", "Orange", "opłaty", Amount(-50.00))
        )
        refreshData()
    }

    private fun refreshData() = with(layoutBinding.paymentList) {
        mSummary = Amount(0.0)
        adapter = PaymentItemAdapter(mItemList, this@MainActivity)
        mItemList.forEach { mSummary.value += it.amount.value }
        layoutBinding.summaryAmount.text = "$mSummary"
    }

    override fun onClick(context: Context, item: Payment) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit")
            .setMessage("editing ${item.amount} ${mItemList.size}")
        dialog.show()
    }

    override fun onLongClick(context: Context, item: Payment) {
        mItemList.remove(item)
        refreshData()
    }
}