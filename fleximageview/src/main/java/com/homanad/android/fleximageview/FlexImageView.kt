package com.homanad.android.fleximageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView

class FlexImageView : AppCompatImageView {

    companion object {
        private val DEFAULT_RADIUS = 0
        private val DEFAULT_BORDER_WIDTH = 0
        private val DEFAULT_BORDER_COLOR = 0
        private val DEFAULT_IS_GRAY_IMAGE = false
        private val GRAY_IMAGE_COLOR_FILTER =
            ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        private val DEFAULT_COLOR_FILTER = 0
        private val DEFAULT_BORDER_DASH_WIDTH = 0
        private val DEFAULT_BORDER_DASH_GAP = 0
        private val DEFAULT_PHRASE = 1f

        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val COLORDRAWABLE_DIMENSION = 2
    }

    private var mCornerTopLeftRadius = DEFAULT_RADIUS
    private var mCornerTopRightRadius = DEFAULT_RADIUS
    private var mCornerBottomRightRadius = DEFAULT_RADIUS
    private var mCornerBottomLeftRadius = DEFAULT_RADIUS
    private var mCornerRadius = DEFAULT_RADIUS
    private var mBorderWidth = DEFAULT_BORDER_WIDTH
    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderDashWidth = DEFAULT_BORDER_DASH_WIDTH
    private var mBorderDashGap = DEFAULT_BORDER_DASH_GAP
    private var mIsGrayImage = DEFAULT_IS_GRAY_IMAGE
    private var mColorFilter = DEFAULT_COLOR_FILTER

    private var bitmap: Bitmap? = null
    private lateinit var bitmapShader: BitmapShader
    private val shaderMatrix = Matrix()

    private var bitmapWidth = 0
    private var bitmapHeight = 0

    private val bitmapPaint = Paint()
    private val bitmapRect = RectF()
    private val borderPaint = Paint()
    private val borderRect = RectF()

    constructor(context: Context?) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0)
            : super(context, attrs, defStyle) {

        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.FlexImageView, defStyle, 0)

        mCornerRadius = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_corner_radius,
            DEFAULT_RADIUS
        )
        mCornerTopLeftRadius = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_corner_topLeft_radius,
            DEFAULT_RADIUS
        )
        mCornerTopRightRadius = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_corner_topRight_radius,
            DEFAULT_RADIUS
        )
        mCornerBottomRightRadius = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_corner_bottomRight_radius,
            DEFAULT_RADIUS
        )
        mCornerBottomLeftRadius = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_corner_bottomLeft_radius,
            DEFAULT_RADIUS
        )

        mBorderWidth = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_border_width,
            DEFAULT_BORDER_WIDTH
        )
        mBorderColor = ta.getColor(
            R.styleable.FlexImageView_border_color,
            DEFAULT_BORDER_COLOR
        )

        mIsGrayImage = ta.getBoolean(
            R.styleable.FlexImageView_gray_image,
            DEFAULT_IS_GRAY_IMAGE
        )

        mColorFilter = ta.getColor(
            R.styleable.FlexImageView_color_filter,
            DEFAULT_COLOR_FILTER
        )

        mBorderDashWidth = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_border_dash_width,
            DEFAULT_BORDER_DASH_WIDTH
        )

        mBorderDashGap = ta.getDimensionPixelSize(
            R.styleable.FlexImageView_border_dash_gap,
            DEFAULT_BORDER_DASH_GAP
        )

        ta.recycle()
    }

    init {
        init()
    }

    private fun init() {
        if (width == 0 && height == 0) {
            return
        }
        if (bitmap == null) {
            invalidate()
            return
        }
        bitmapShader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        bitmapPaint.isAntiAlias = true
        bitmapPaint.isDither = true
        bitmapPaint.isFilterBitmap = true
        bitmapPaint.shader = bitmapShader

        if (mIsGrayImage) {
            bitmapPaint.colorFilter = GRAY_IMAGE_COLOR_FILTER
        } else if (mColorFilter != DEFAULT_COLOR_FILTER) {
            bitmapPaint.colorFilter = createColorFilter(mColorFilter)
        }

        bitmapHeight = bitmap!!.height
        bitmapWidth = bitmap!!.width

        borderPaint.style = Paint.Style.STROKE
        borderPaint.isAntiAlias = true
        borderPaint.color = mBorderColor
        borderPaint.strokeWidth = mBorderWidth.toFloat()

        createAndSetDashPathEffect(mBorderDashWidth, mBorderDashGap)

        bitmapRect.set(calculateBounds())
        borderRect.set(calculateBounds())

        updateShaderMatrix()
        invalidate()
    }

    private fun createColorFilter(color: Int): PorterDuffColorFilter {
        return PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun createAndSetDashPathEffect(dashWidth: Int, dashGap: Int) {
        if (dashWidth != DEFAULT_BORDER_DASH_WIDTH && dashGap != DEFAULT_BORDER_DASH_GAP) {
            borderPaint.pathEffect = DashPathEffect(
                floatArrayOf(dashWidth.toFloat(), dashGap.toFloat()),
                DEFAULT_PHRASE
            )
        }
    }

    var cornerRadius: Int
        get() = mCornerRadius
        set(value) {
            if (value == mCornerRadius) {
                return
            }
            mCornerRadius = value
            invalidate()
        }

    var cornerTopLeftRadius: Int
        get() = mCornerTopLeftRadius
        set(value) {
            if (value == mCornerTopLeftRadius) {
                return
            }
            mCornerTopLeftRadius = value
            invalidate()
        }

    var cornerTopRightRadius: Int
        get() = mCornerTopRightRadius
        set(value) {
            if (value == mCornerTopRightRadius) {
                return
            }
            mCornerTopRightRadius = value
            invalidate()
        }

    var cornerBottomRightRadius: Int
        get() = mCornerBottomRightRadius
        set(value) {
            if (value == mCornerBottomRightRadius) {
                return
            }
            mCornerBottomRightRadius = value
            invalidate()
        }

    var cornerBottomLeftRadius: Int
        get() = mCornerBottomLeftRadius
        set(value) {
            if (value == mCornerBottomLeftRadius) {
                return
            }
            mCornerBottomLeftRadius = value
            invalidate()
        }

    var borderWidth: Int
        get() = mBorderWidth
        set(value) {
            if (value == mBorderWidth) {
                return
            }
            mBorderWidth = value
            borderPaint.strokeWidth = mBorderWidth.toFloat()
            invalidate()
        }

    var borderColor: Int
        get() = mBorderColor
        set(borderColor) {
            if (borderColor == mBorderColor) {
                return
            }
            mBorderColor = borderColor
            borderPaint.color = mBorderColor
            invalidate()
        }

    var isGrayImage: Boolean
        get() = mIsGrayImage
        set(value) {
            if (value == mIsGrayImage) {
                return
            }
            mIsGrayImage = value
            bitmapPaint.colorFilter = GRAY_IMAGE_COLOR_FILTER
            invalidate()
        }

    var filterColor: Int
        get() = mColorFilter
        set(value) {
            if (value == mColorFilter) {
                return
            }
            mColorFilter = value
            bitmapPaint.colorFilter = createColorFilter(mColorFilter)
        }

    var borderDashWidth: Int
        get() = mBorderDashWidth
        set(value) {
            if (value == mBorderDashWidth) {
                return
            }
            mBorderDashWidth = value
            createAndSetDashPathEffect(value, mBorderDashGap)
            invalidate()
        }

    var borderDashGap: Int
        get() = mBorderDashGap
        set(value) {
            if (value == mBorderDashGap) {
                return
            }
            mBorderDashGap = value
            createAndSetDashPathEffect(mBorderDashWidth, value)
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        if (bitmap == null) {
            return
        }

        if (mCornerRadius > DEFAULT_RADIUS) {
            canvas.drawRoundRect(
                bitmapRect,
                mCornerRadius.toFloat(),
                mCornerRadius.toFloat(),
                bitmapPaint
            )
            canvas.drawRoundRect(
                borderRect,
                mCornerRadius.toFloat(),
                mCornerRadius.toFloat(),
                borderPaint
            )
        } else {
            val radii = floatArrayOf(
                mCornerTopLeftRadius.toFloat(),
                mCornerTopLeftRadius.toFloat(),
                mCornerTopRightRadius.toFloat(),
                mCornerTopRightRadius.toFloat(),
                mCornerBottomRightRadius.toFloat(),
                mCornerBottomRightRadius.toFloat(),
                mCornerBottomLeftRadius.toFloat(),
                mCornerBottomLeftRadius.toFloat()
            )
            val path = Path().apply { addRoundRect(bitmapRect, radii, Path.Direction.CW) }
            canvas.drawPath(path, bitmapPaint)
            canvas.drawPath(path, borderPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        init()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        init()
    }

    private fun calculateBounds(): RectF {
        /*unneccessary*/
//        if (width == height) {
//            val availableWidth = width - paddingLeft - paddingRight
//            val availableHeight = height - paddingTop - paddingBottom
//            val maxLength = Math.min(availableWidth, availableHeight)
//            val left = paddingLeft + (availableWidth - maxLength) / 2f
//            val top = paddingTop + (availableHeight - maxLength) / 2f
//            return RectF(left, top, left + maxLength, top + maxLength)
//        } else {
        val availableWidth = width - paddingLeft - paddingRight.toFloat() - borderWidth / 2
        val availableHeight = height - paddingTop - paddingBottom.toFloat() - borderWidth / 2
        val left = paddingLeft.toFloat() + borderWidth / 2
        val top = paddingTop.toFloat() + borderWidth / 2
        return RectF(left, top, availableWidth, availableHeight)
//        }
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        shaderMatrix.set(null)
        if (bitmapWidth * bitmapRect.height() > bitmapRect.width() * bitmapHeight) {
            scale = bitmapRect.height() / bitmapHeight.toFloat()
            dx = (bitmapRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = bitmapRect.width() / bitmapWidth.toFloat()
            dy = (bitmapRect.height() - bitmapHeight * scale) * 0.5f
        }
        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + bitmapRect.left,
            (dy + 0.5f).toInt() + bitmapRect.top
        )
        bitmapShader.setLocalMatrix(shaderMatrix)
    }

    private fun initializeBitmap() {
        bitmap = getBitmapFromDrawable(drawable)
        init()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap
            bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLORDRAWABLE_DIMENSION,
                    COLORDRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }
}