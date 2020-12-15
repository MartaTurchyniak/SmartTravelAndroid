package com.magic.smarttravel.screens.general

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Marta Turchyniak on 12/1/20.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class DefaultAdapter<Item, ViewHolder : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<ViewHolder>() {

    protected val items = mutableListOf<Item>()

    override fun getItemCount(): Int = items.size

    fun items(): List<Item> {
        return this.items
    }

    fun update(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
}