// source: https://medium.com/over-engineering/detecting-snap-changes-with-androids-recyclerview-snaphelper-9e9f5e95c424
package com.example.mobdevemco


import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
class SnapOnScrollListener(
    private val snapHelper: SnapHelper,
    var behavior: Int = NOTIFY_ON_SCROLL,
    var onSnapPositionChangeListener: ((position: Int) -> Unit)? = null
) : RecyclerView.OnScrollListener() {

    companion object {
        const val NOTIFY_ON_SCROLL = 0
        const val NOTIFY_ON_SCROLL_STATE_IDLE = 1
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == NOTIFY_ON_SCROLL) {
            dispatchSnapPositionChange(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == NOTIFY_ON_SCROLL_STATE_IDLE
            && newState == RecyclerView.SCROLL_STATE_IDLE) {
            dispatchSnapPositionChange(recyclerView)
        }
    }

    private fun dispatchSnapPositionChange(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager ?: return
        val snapView = snapHelper.findSnapView(layoutManager) ?: return
        val snapPosition = layoutManager.getPosition(snapView)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.invoke(snapPosition)
            this.snapPosition = snapPosition
        }
    }
}