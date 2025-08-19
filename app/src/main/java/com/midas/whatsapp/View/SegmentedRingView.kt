package com.midas.whatsapp.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class SegmentedRingView @JvmOverloads  constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): View(context, attrs, defStyleAttr){
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f    // Thickness of the ring
    }

    private val rectF = RectF()
    private val segments = 5    // Number of segments to draw
    private val gapAngle = 5f   // Gap between segments

    var segmentsCompleted = 3
    var completedColor = Color.parseColor("#25D366")
    var incompleteColor = Color.parseColor("#CCCCCC")

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size =  MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val strokeWidth = ringPaint.strokeWidth
        val padding = strokeWidth / 2
        rectF.set(padding, padding, width - padding, height - padding)

        val totalSegmentAngle = 360f / segments

        val segmentSweepAngle = totalSegmentAngle - gapAngle

        for(i in 0 until  segments){
            val startAngle = (i * totalSegmentAngle) + (gapAngle / 2)

            if(i < segmentsCompleted){
                ringPaint.color = completedColor
            }else{
                ringPaint.color = incompleteColor
            }
            canvas.drawArc(rectF, startAngle, segmentSweepAngle, false, ringPaint)
        }
    }
}