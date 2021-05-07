package com.jk.prm.financemanager

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.jk.prm.financemanager.databinding.ActivityChartBinding
import com.jk.prm.financemanager.model.Payment
import com.jk.prm.financemanager.utils.Converter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChartActivity : AppCompatActivity() {
    private val viewBind by lazy { ActivityChartBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBind.root)

//        val amounts = intent.getDoubleArrayExtra("amounts")!!
//        val dates: Array<String> = intent.getStringArrayExtra("dates")!!
        val payments: ArrayList<Payment> = intent.getParcelableArrayListExtra("payments")!!

        setupSpinner(payments)
    }

    private fun setupSpinner(payments: ArrayList<Payment>) =
        with(viewBind.monthSpinner) {
            val months = setSpinnerItems(payments.map { it.date!! })

            this.adapter = ArrayAdapter(
                this@ChartActivity,
                R.layout.support_simple_spinner_dropdown_item,
                months
            )

            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    val (month, year) = Converter.convertToMonthAndYear(
                        months[position],
                        "yyyy MMMM"
                    )

                    val xValues: IntArray = prepareXValues(id, day, month, year)
                    val yValues: DoubleArray = prepareYValues(payments, xValues.size, month, year)

                    viewBind.graphView.setParams(xValues, yValues)
                    viewBind.graphView.invalidate()
                }

                private fun prepareXValues(id: Long, day: Int, month: Int, year: Int): IntArray {
                    val values: IntArray
                    val number =
                        GregorianCalendar(year, month, day).getActualMaximum(Calendar.DAY_OF_MONTH)

                    values =
                        if (id == 0L) IntArray(day)
                        else IntArray(number)

                    for (i in values.indices) values[i] = i + 1
                    return values
                }

                private fun prepareYValues(
                    payments: ArrayList<Payment>,
                    size: Int,
                    month: Int,
                    year: Int
                ): DoubleArray {
                    val filtered = payments.filter {
                        Converter.convertToMonthAndYear(it.date!!, "yyyy-MM-dd") == Pair(
                            month,
                            year
                        )
                                && Converter.convertToDay(it.date, "yyyy-MM-dd") <= size
                    }.sortedBy { it.date }

                    val result = DoubleArray(size)
                    var sum = 0.0
                    var counter = 0
                    for (i in 0 until size) {
                        if (i + 1 == Converter.convertToDay(
                                filtered[counter].date!!,
                                "yyyy-MM-dd"
                            )
                        ) {
                            sum += filtered[counter].amount
                            if (counter != filtered.size - 1) counter++
                        }
                        result[i] = sum
                    }
                    return result
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

    private fun setSpinnerItems(dates: List<String>): Array<String> {
        return with(dates) {
            val dateParse = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateDisplay = SimpleDateFormat("yyyy MMMM", Locale.US)
            val set: MutableSet<String> = sortedSetOf()

            this.map { dateDisplay.format(dateParse.parse(it)!!) }
                .forEach { set.add(it) }
            set.sortedDescending().toTypedArray()
        }
    }
}