package com.magic.smarttravel.screens.general

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Marta Turchyniak on 12/1/20.
 */
abstract class CommonViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    @CallSuper
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    open fun bind(data: T) {
        itemView.contentDescription = adapterPosition.toString()
    }
}