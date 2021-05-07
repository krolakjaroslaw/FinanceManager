package com.jk.prm.financemanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jk.prm.financemanager.adapter.PaymentItemAdapter
import com.jk.prm.financemanager.adapter.PaymentItemViewHolder
import com.jk.prm.financemanager.database.PaymentDatabase
import com.jk.prm.financemanager.databinding.ActivityMainBinding
import com.jk.prm.financemanager.model.Payment
import com.jk.prm.financemanager.utils.Converter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

private const val REQUEST_EDIT_PAYMENT = 1
private const val REQUEST_SHOW_CHART = 2

private const val AMOUNT = "amount"
private const val CATEGORY = "category"
private const val DATE = "date"
private const val EDIT = "edit"
private const val ID = "id"
private const val NAME = "name"

class MainActivity : AppCompatActivity(), PaymentItemAdapter.OnClickListener {
    private val view by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val db by lazy { PaymentDatabase.open(this) }
    private val paymentAdapter by lazy { PaymentItemAdapter(db, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        setupPaymentList()
        setupAddPaymentButton()
        setupExpensesButton()
    }

    private fun setupExpensesButton() = view.expensesBtn.setOnClickListener {
        val intent = Intent(this, ChartActivity::class.java)
//        val amounts = paymentAdapter.items.map { it.amount.value }.toDoubleArray()
//        val amounts = paymentAdapter.items.map { it.amount }.toDoubleArray()
//        val dates = paymentAdapter.items.map { it.date }.toTypedArray()
//        intent.putExtra("amounts", amounts)
//        intent.putExtra("dates", dates)
        intent.putParcelableArrayListExtra("payments", paymentAdapter.items as ArrayList<Payment>)
        // TODO: refactor to launch()
        startActivityForResult(
            intent, REQUEST_SHOW_CHART
        )
    }

    private fun setupAddPaymentButton() = view.addItemBtn.setOnClickListener {
        val intent = Intent(this, EditPaymentActivity::class.java)
        // TODO: refactor to launch()
        startActivityForResult(
            intent, REQUEST_EDIT_PAYMENT
        )
    }

    private fun setupPaymentList() = with(view.paymentList) {
        this.apply {
            adapter = paymentAdapter
            layoutManager = LinearLayoutManager(context)
        }
        refreshData()
        // TODO: items divider
//        this.addItemDecoration(
//                DividerItemDecoration(this@MainActivity, LinearLayoutManager.HORIZONTAL)
//        )
    }

    private fun refreshData() = thread {
        paymentAdapter.load()

        runOnUiThread {
//            val mSummary: Amount = Amount(0.0)
            var mSummary = 0.0
//            paymentAdapter.items.forEach { mSummary.value += it.amount.value }
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val year = Calendar.getInstance().get(Calendar.YEAR)
            paymentAdapter.items
                .filter { Converter.convertToMonthAndYear(it.date!!, "yyyy-MM-dd") == Pair(month, year) }
//                .filter { convertToMonthAndYear(it.date!!, "yyyy-MM-dd") == Pair(month, year) }
                .forEach { mSummary += it.amount }

            val summary = String.format("%.2f", mSummary) + " zł"
            view.summaryAmount.text = summary
        }
    }

    override fun onClick(context: Context, item: Payment) {
        val intent = Intent(this, EditPaymentActivity::class.java)
        intent.putExtra(EDIT, true)
        intent.putExtra(ID, item.id)
        intent.putExtra(NAME, item.name)
        intent.putExtra(CATEGORY, item.category)
//        intent.putExtra(AMOUNT, item.amount.value)
        intent.putExtra(AMOUNT, item.amount)
        intent.putExtra(DATE, item.date)

        // TODO: refactor to launch()
        startActivityForResult(
            intent, REQUEST_EDIT_PAYMENT
        )
    }

    override fun onLongClick(context: Context, holder: PaymentItemViewHolder) {
        thread {
            paymentAdapter.deleteItem(holder.layoutPosition)
            refreshData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == REQUEST_EDIT_PAYMENT || requestCode == REQUEST_SHOW_CHART) &&
            resultCode == Activity.RESULT_OK
        ) {
            refreshData()
            // TODO: refactor to launch()
        } else super.onActivityResult(requestCode, resultCode, data)
    }
}