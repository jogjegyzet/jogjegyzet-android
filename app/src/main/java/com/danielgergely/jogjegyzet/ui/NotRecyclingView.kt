package com.danielgergely.jogjegyzet.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.LinearLayout

class NotRecyclingView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    private var items = mutableListOf<RecyclerView.ViewHolder>()

    var adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null
        set(value) {
            field?.unregisterAdapterDataObserver(observer)

            field = value!!
            value.registerAdapterDataObserver(observer)
            refresh()
        }

    private var observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = refresh()
    }

    private fun refresh() {
        val adapter = adapter!!

        val count = adapter.itemCount

        val newItems = mutableListOf<RecyclerView.ViewHolder>()

        this.removeAllViewsInLayout()

        for (i in 0 until count) {
            val type = adapter.getItemViewType(i)

            var item = items.firstOrNull { it.itemViewType == type }
            if (item != null) {
                items.remove(item)
            } else {
                item = adapter.onCreateViewHolder(this, type)!!
            }

            //TODO better solution
            (adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>).onBindViewHolder(item, i)
            newItems.add(item)
        }

        newItems.forEachIndexed { index, viewHolder ->  addView(viewHolder.itemView, index) }
        items = newItems

        invalidate()
        requestLayout()
    }

}