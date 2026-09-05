package com.example.booksy

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class SwipeToDeleteCallback(
    private val onSwiped: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            onSwiped(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val background = ColorDrawable(
            ContextCompat.getColor(recyclerView.context, R.color.color_favorito)
        )
        val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)
        icon?.setTint(Color.WHITE)

        when {
            dX > 0 -> background.setBounds(
                itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom
            )
            dX < 0 -> background.setBounds(
                itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom
            )
            else -> background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)

        icon?.let {
            val iconMargin = (itemView.height - it.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + it.intrinsicHeight

            when {
                dX > 0 -> {
                    val iconLeft = itemView.left + iconMargin
                    it.setBounds(iconLeft, iconTop, iconLeft + it.intrinsicWidth, iconBottom)
                }
                dX < 0 -> {
                    val iconRight = itemView.right - iconMargin
                    it.setBounds(iconRight - it.intrinsicWidth, iconTop, iconRight, iconBottom)
                }
                else -> it.setBounds(0, 0, 0, 0)
            }
            it.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}