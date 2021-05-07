package com.jk.prm.financemanager.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import kotlin.math.abs

class ChartView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val yAxeMargin = 130F
    private val widthPadding = 50F
    private val heightPadding = 10
    private var paint = Paint().apply {
        color = Color.BLACK
        textSize = 40F
        typeface = Typeface.MONOSPACE
    }

    private var xValues: IntArray = IntArray(2)
    private var yValues: DoubleArray = DoubleArray(2)
    private var yMin: Float = 0F
    private var yMax: Float = 0F
    private var vectorLength = 2

    fun setParams(xArr: IntArray, yArr: DoubleArray) {
//        println("params: ${xArr.size}")
//        println("params: ${yArr.size}")
        xValues = xArr
        yValues = yArr
        yMax =
            if (yValues.maxOrNull()!! < 0) 0F
            else yValues.maxOrNull()!!.toFloat()
        yMin = yValues.minOrNull()!!.toFloat()
        vectorLength = xValues.size
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawARGB(255, 255, 255, 255)

        val xStep = (width - yAxeMargin - widthPadding) / (vectorLength - 1)
//        println("xStep: $xStep")

        val ySize =
            if (yMin > 0 && yMax > 0) yMax
            else if (yMin < 0 && yMax < 0) abs(yMin)
            else yMax - yMin
//        println("ySize: $ySize")

        val yZero =
            if (yMin > 0 && yMax > 0) (height - heightPadding).toFloat()
            else if (yMin < 0 && yMax < 0) 0F
            else yMax * (height - heightPadding) / ySize
//        println("yZero: $yZero")

        drawAxes(canvas, yZero)

        drawAxesDescription(canvas, xStep, yZero, ySize)

        drawChart(xStep, ySize, yZero, canvas)
    }

    private fun drawAxes(canvas: Canvas?, yZero: Float) {
        paint.color = Color.BLACK
        paint.strokeWidth = 10F

        canvas?.drawLine(yAxeMargin, 0F, yAxeMargin, (height - heightPadding).toFloat(), paint)
        canvas?.drawLine(yAxeMargin, yZero, (width - widthPadding), yZero, paint)
    }

    private fun drawAxesDescription(canvas: Canvas?, xStep: Float, yZero: Float, ySize: Float) {
        // xAxe
        val stepH = vectorLength / 11 + 1

        var i = 0
        while (i < vectorLength) {
            canvas?.drawText("${i + 1}", yAxeMargin + xStep * i, yZero + 50F, paint)
            i += stepH
        }

        //yAxe
        val stepV = 100 * (ySize.toInt() / 2000 + 1)
        canvas?.drawText("0", 0F, yZero, paint)
        val yStep = stepV / ySize * (height - heightPadding)
        var j = 0
        while (j * stepV <= abs(yMax)) {
            canvas?.drawText("${stepV * j}", 0F, yZero - yStep * j, paint)
            j++
        }
        j = 0
        while (j * stepV <= abs(yMin)) {
            canvas?.drawText("${stepV * j * -1}", 0F, yZero + yStep * j, paint)
            j++
        }
        canvas?.drawText("zł", yAxeMargin + 25F, 3 * heightPadding.toFloat(), paint)
    }

    private fun drawChart(xStep: Float, ySize: Float, yZero: Float, canvas: Canvas?) {
        paint.strokeWidth = 7.5F
        for (i in 0 until vectorLength - 1) {
            val xStart: Float = xStep * i
            val xStop: Float = xStep * (i + 1)
            val yStart: Float = ((yMax - yValues[i]) / ySize * (height - heightPadding)).toFloat()
            val yStop: Float =
                ((yMax - yValues[i + 1]) / ySize * (height - heightPadding)).toFloat()

            if (yStart <= yZero && yStop <= yZero) {
                paint.color = Color.GREEN
                canvas?.drawLine(yAxeMargin + xStart, yStart, yAxeMargin + xStop, yStop, paint)
            } else if (yStart >= yZero && yStop >= yZero) {
                paint.color = Color.RED
                canvas?.drawLine(yAxeMargin + xStart, yStart, yAxeMargin + xStop, yStop, paint)
            } else {
                val yDiff: Float = abs(yZero - yStart)
                val tan: Float = abs(yStop - yStart) / xStep
                val xZero: Float = yDiff / tan + xStep * i

                paint.color =
                    if (yZero < yStart) Color.RED
                    else Color.GREEN
                canvas?.drawLine(yAxeMargin + xStart, yStart, yAxeMargin + xZero, yZero, paint)

                paint.color =
                    if (yZero < yStart) Color.GREEN
                    else Color.RED
                canvas?.drawLine(yAxeMargin + xZero, yZero, yAxeMargin + xStop, yStop, paint)
            }
        }
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            widthMeasureSpec - marginLeft - marginRight,
            heightMeasureSpec - marginTop - marginBottom
        )
//        println("margins: $marginLeft $marginRight $marginTop $marginBottom")
//        println("padding: $paddingLeft $paddingRight $paddingTop $paddingBottom")
    }
}
