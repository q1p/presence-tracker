package me.venko.presencetracker.ui.views

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import me.venko.presencetracker.R

class RadiusOverlayView : LinearLayout {
    private var windowFrame: Bitmap? = null
    private var outerColor = Color.TRANSPARENT
    private var radiusMargin = 0

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributes(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttributes(attrs)
    }

    private fun initAttributes(attrs: AttributeSet) {

        var typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.RadiusOverlayView, 0, 0)

        outerColor = typedArray.getColor(R.styleable.RadiusOverlayView_outerColor, Color.TRANSPARENT)
        radiusMargin = typedArray.getDimensionPixelSize(R.styleable.RadiusOverlayView_radiusMargin, 0)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        if (windowFrame == null) {
            createWindowFrame() // Lazy creation of the window frame, this is needed as we don't know the width & height of the screen until draw time
        }

        canvas.drawBitmap(windowFrame!!, 0f, 0f, null)
    }

    private fun createWindowFrame() {
        windowFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val osCanvas = Canvas(windowFrame!!)

        val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = outerColor
        paint.alpha = 84
        osCanvas.drawRect(outerRectangle, paint)

        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val radius = (Math.min(width, height) / 2 - radiusMargin).toFloat()
        osCanvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun isInEditMode(): Boolean {
        return true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        windowFrame = null
    }
}