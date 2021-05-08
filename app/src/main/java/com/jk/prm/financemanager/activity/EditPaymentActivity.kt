package com.jk.prm.financemanager.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.jk.prm.financemanager.R
import com.jk.prm.financemanager.database.PaymentDatabase
import com.jk.prm.financemanager.database.PaymentDto
import com.jk.prm.financemanager.databinding.ActivityEditPaymentBinding
import java.util.concurrent.Executors

private const val AMOUNT = "amount"
private const val CATEGORY = "category"
private const val DATE = "date"
private const val EDIT = "edit"
private const val ID = "id"
private const val NAME = "name"

private const val EDIT_PAYMENT = "Edit payment"

class EditPaymentActivity : AppCompatActivity() {
    private val view by lazy { ActivityEditPaymentBinding.inflate(layoutInflater) }
    private val pool by lazy { Executors.newSingleThreadExecutor() }
    private val db by lazy { PaymentDatabase.open(this) }

    private var edit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        edit = intent.getBooleanExtra(EDIT, false)

        setupPayment()
        setupSave()
    }

    override fun onDestroy() {
        super.onDestroy()
        pool.shutdownNow()
    }

    private fun setupPayment() {
        if (edit) {
            val name: String? = intent.getStringExtra(NAME)
            val category: String? = intent.getStringExtra(CATEGORY)
            val amount: Double = intent.getDoubleExtra(AMOUNT, 0.0)
            val date: String? = intent.getStringExtra(DATE)
            view.titleText.text = EDIT_PAYMENT
            view.name.setText(name)
            view.category.setText(category)
            view.typeSwitch.isChecked = amount > 0
            view.amount.setText(String.format("%.2f", amount).substringAfter("-"))
            view.date.setText(date)
        }
    }

    private fun setupSave() = view.saveBtn.setOnClickListener {
        if (!isValid()) return@setOnClickListener
        var amountValue = view.amount.text.toString().toDouble()
        if (!view.typeSwitch.isChecked) amountValue = (amountValue * -1)

        val paymentDto = PaymentDto(
            name = view.name.text.toString(),
            category = view.category.text.toString(),
            amount = amountValue,
            date = view.date.text.toString()
        )
        val id = intent.getLongExtra(ID, 0)
        pool.submit {
            if (edit) db.payments.updateById(
                id = id,
                name = paymentDto.name,
                category = paymentDto.category,
                amount = paymentDto.amount,
                date = paymentDto.date
            )
            else db.payments.insert(paymentDto)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun isValid(): Boolean {
        var message = "Please fill name field."
        if (view.name.text.isBlank()) {
            showSnackBar(message, Snackbar.LENGTH_LONG)
            return false
        }

        val amountRegex = "^\\d+([.,]?)\\d{0,2}+$".toRegex()
        message = "Amount value is incorrect."
        if (!amountRegex.matches(view.amount.text)) {
            showSnackBar(message, Snackbar.LENGTH_SHORT)
            return false
        }

        val dateRegex = "^(19|20)\\d\\d(-)(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])\$".toRegex()
        message = "Date format is incorrect. Please provide valid data: YYYY-MM-DD"
        if (!dateRegex.matches(view.date.text)) {
            showSnackBar(message, Snackbar.LENGTH_LONG)
            return false
        }
        return true
    }

    private fun showSnackBar(message: String, length: Int) {
        val snackBar: Snackbar = Snackbar.make(view.root, message, length)
        snackBar.setBackgroundTint(Color.RED)
        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_payment_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.share) {
            if (!isValid()) return false
            val sign: String = if (view.typeSwitch.isChecked) "+" else "-"
            val content: String = "$NAME: ${view.name.text} \n" +
                    "$CATEGORY: ${view.category.text} \n" +
                    "$AMOUNT: $sign${view.amount.text} \n" +
                    "$DATE: ${view.date.text}"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, content)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}