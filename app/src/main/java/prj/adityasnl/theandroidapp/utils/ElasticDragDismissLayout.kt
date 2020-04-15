package prj.adityasnl.theandroidapp.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import prj.adityasnl.theandroidapp.R
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.min


class ElasticDragDismissLayout: FrameLayout {

    private var dragDismissDistance = Float.MAX_VALUE
    private var dragDismissFraction = -1f
    private var dragDismissScale = 1f
    private var shouldScale = false
    private var dragElasticity = 0.8f

    // state
    private var totalDrag = 0f
    private var draggingDown = false
    private var draggingUp = false
    private var mLastActionEvent = 0

    private var callbacks: ArrayList<ElasticDragDismissCallback>? = null

    constructor(context: Context) : this(context, null, 0, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ElasticDragDismissLayout, 0, 0)

        if (a.hasValue(R.styleable.ElasticDragDismissLayout_dragDismissDistance)) {
            dragDismissDistance = a.getDimensionPixelSize(R.styleable.ElasticDragDismissLayout_dragDismissDistance, 0).toFloat()
        } else if (a.hasValue(R.styleable.ElasticDragDismissLayout_dragDismissFraction)) {
            dragDismissFraction = a.getFloat(R.styleable.ElasticDragDismissLayout_dragDismissFraction, dragDismissFraction)
        }
        if (a.hasValue(R.styleable.ElasticDragDismissLayout_dragDismissScale)) {
            dragDismissScale = a.getFloat(R.styleable.ElasticDragDismissLayout_dragDismissScale, dragDismissScale)
            shouldScale = dragDismissScale != 1f
        }
        if (a.hasValue(R.styleable.ElasticDragDismissLayout_dragElasticity)) {
            dragElasticity = a.getFloat(R.styleable.ElasticDragDismissLayout_dragElasticity, dragElasticity)
        }

        a.recycle()
    }

    abstract class ElasticDragDismissCallback {
        /**
         * Called for each drag event.
         *
         * @param elasticOffset       Indicating the drag offset with elasticity applied i.e. may
         * exceed 1.
         * @param elasticOffsetPixels The elastically scaled drag distance in pixels.
         * @param rawOffset           Value from [0, 1] indicating the raw drag offset i.e.
         * without elasticity applied. A value of 1 indicates that the
         * dismiss distance has been reached.
         * @param rawOffsetPixels     The raw distance the user has dragged
         */
        open fun onDrag(elasticOffset: Float, elasticOffsetPixels: Float, rawOffset: Float, rawOffsetPixels: Float) {}

        /**
         * Called when dragging is released and has exceeded the threshold dismiss distance.
         */
        open fun onDragDismissed() {}
    }

    override fun onStartNestedScroll(child: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return (nestedScrollAxes and View.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray?) {
        // if we're in a drag gesture and the user reverses up the we should take those events
        if (draggingDown && dy > 0 || draggingUp && dy < 0) {
            dragScale(dy)
            consumed?.set(1, dy)
        }
    }

    override fun onNestedScroll(
        target: View?,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
       dragScale(dyUnconsumed)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        mLastActionEvent = ev.action
        return super.onInterceptTouchEvent(ev)
    }

    override fun onStopNestedScroll(child: View?) {
        if (abs(totalDrag) >= dragDismissDistance) {
            dispatchDismissCallback()
        } else { // settle back to natural position
            if (mLastActionEvent == MotionEvent.ACTION_DOWN) {
                translationY = 0f
                scaleX = 1f
                scaleY = 1f
            } else {
                animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200L)
                    .setInterpolator(AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in))
                    .setListener(null)
                    .start()
            }
            totalDrag = 0f
            draggingUp = false
            draggingDown = draggingUp
            dispatchDragCallback(0f, 0f, 0f, 0f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction
        }
    }

    fun addListener(listener: ElasticDragDismissCallback) {
        if (callbacks == null) {
            callbacks = ArrayList()
        }
        callbacks!!.add(listener)
    }

    fun removeListener(listener: ElasticDragDismissCallback) {
        if (callbacks != null && callbacks!!.size > 0) {
            callbacks!!.remove(listener)
        }
    }

    private fun dragScale(scroll: Int) {
        if (scroll == 0) return
        totalDrag += scroll.toFloat()

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true
            if (shouldScale) pivotY = height.toFloat()
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true
            if (shouldScale) pivotY = 0f
        }
        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        var dragFraction =
            log10(1 + (abs(totalDrag) / dragDismissDistance).toDouble()).toFloat()

        // calculate the desired translation given the drag fraction
        var dragTo: Float = dragFraction * dragDismissDistance * dragElasticity
        if (draggingUp) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= -1f
        }
        translationY = dragTo
        if (shouldScale) {
            val scale = 1 - (1 - dragDismissScale) * dragFraction
            scaleX = scale
            scaleY = scale
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        if (draggingDown && totalDrag >= 0
            || draggingUp && totalDrag <= 0
        ) {
            dragFraction = 0f
            dragTo = dragFraction
            totalDrag = dragTo
            draggingUp = false
            draggingDown = draggingUp
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
        }
        dispatchDragCallback(
            dragFraction, dragTo,
            min(1f, abs(totalDrag) / dragDismissDistance), totalDrag
        )
    }

    private fun dispatchDragCallback(
        elasticOffset: Float, elasticOffsetPixels: Float,
        rawOffset: Float, rawOffsetPixels: Float
    ) {
        if (callbacks != null && callbacks!!.isNotEmpty()) {
            for (callback in callbacks!!) {
                callback.onDrag(
                    elasticOffset, elasticOffsetPixels,
                    rawOffset, rawOffsetPixels
                )
            }
        }
    }

    private fun dispatchDismissCallback() {
        if (callbacks != null && callbacks!!.isNotEmpty()) {
            for (callback in callbacks!!) {
                callback.onDragDismissed()
            }
        }
    }

    open class SystemChromeFader(private val activity: Activity) : ElasticDragDismissCallback() {
        private val statusBarAlpha: Int = Color.alpha(activity.window.statusBarColor)
        private val navBarAlpha: Int = Color.alpha(activity.window.navigationBarColor)
        private val fadeNavBar: Boolean = Utils.isNavBarOnBottom(activity)

        override
        fun onDrag(elasticOffset: Float, elasticOffsetPixels: Float, rawOffset: Float, rawOffsetPixels: Float) {
            when {
                elasticOffsetPixels > 0 -> {
                    // dragging downward, fade the status bar in proportion
                    activity.window.statusBarColor = Utils.modifyAlpha(activity.window.statusBarColor, ((1f - rawOffset) * statusBarAlpha).toInt())
                }
                elasticOffsetPixels == 0f -> {
                    // reset
                    activity.window.statusBarColor = Utils.modifyAlpha(activity.window.statusBarColor, statusBarAlpha)
                    activity.window.navigationBarColor = Utils.modifyAlpha(activity.window.navigationBarColor, navBarAlpha)
                }
                fadeNavBar -> {
                    // dragging upward, fade the navigation bar in proportion
                    activity.window.navigationBarColor = Utils.modifyAlpha(activity.window.navigationBarColor, ((1f - rawOffset) * navBarAlpha).toInt())
                }
            }
        }

        override fun onDragDismissed() {
            activity.finishAfterTransition()
        }
    }
}