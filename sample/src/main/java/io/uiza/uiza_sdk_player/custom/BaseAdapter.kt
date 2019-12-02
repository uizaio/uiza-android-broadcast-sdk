package io.uiza.uiza_sdk_player.custom


import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.uiza.extensions.inflate
import io.uiza.extensions.onClick


/**
 *
 * Created by namnd on 10/21/17.
 */
abstract class BaseAdapter<ITEM>(private var itemList: List<ITEM>,
                                 @LayoutRes private val itemLayoutResId: Int,
                                 @LayoutRes private val headerLayoutResId: Int = 0,
                                 @LayoutRes private val footerLayoutResId: Int = 0,
                                 @LayoutRes private val emptyLayoutResId: Int = 0,
                                 private val emptyCount: Int = 1)
    : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

    var numberPerPage: Int = 8

    var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    open fun getCount(): Int = itemList.size

    /**
     * Returns data associated with this adapter.
     *
     * @return adapter data.
     */
    @SuppressWarnings("WeakerAccess")
    open fun getData(): List<ITEM> = itemList

    final override fun getItemCount(): Int {
        return when {
            itemList.isEmpty() -> viewCountWhenEmptyItems()
            else -> itemList.size + viewMoreCount()
        }
    }

    @SuppressWarnings("WeakerAccess")
    open fun getItem(position: Int): ITEM? = if (itemList.isNotEmpty() && position >= 0 && position < getCount()) itemList[position] else null

    final override fun getItemViewType(position: Int): Int {
        return if (itemList.isEmpty()) {
            if (position == 0 && headerVisible()) TYPE_HEADER
            else if (emptyVisible()) TYPE_EMPTY
            else TYPE_NONE
        } else {
            if (position == 0 && headerVisible()) TYPE_HEADER
            else if (position == (itemList.size + offset()) && footerVisible()) TYPE_FOOTER
            else TYPE_ITEM_LOADED
        }

    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            TYPE_HEADER -> {
                val headerView = parent inflate headerLayoutResId
                return BaseViewHolder(
                    headerView
                )
            }
            TYPE_FOOTER -> {
                val footer = parent inflate footerLayoutResId
                return BaseViewHolder(
                    footer
                )
            }
            TYPE_EMPTY -> {
                val emptyView = parent inflate emptyLayoutResId
                return BaseViewHolder(
                    emptyView
                )
            }
            TYPE_ITEM_LOADED -> {
                val view = parent inflate itemLayoutResId
                val viewHolder =
                    BaseViewHolder(
                        view
                    )
                val itemView = viewHolder.itemView
                itemView.onClick {
                    val adapterPosition = viewHolder.adapterPosition
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val realPos = adapterPosition - offset()
                        if (realPos >= 0)
                            onItemClick(itemView, realPos)
                    }
                }
                return viewHolder
            }
            else -> throw IllegalArgumentException("Error view type $viewType")
        }
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEADER -> holder.itemView.bindHeader()
            TYPE_FOOTER -> holder.itemView.bindFooter()
            TYPE_EMPTY -> holder.itemView.bindEmpty()
            TYPE_ITEM_LOADED -> {
                val realPos = position - offset()
                if (realPos < itemList.size)
                    itemList[realPos]?.let { item ->
                        holder.itemView.bind(item, realPos)
                    }
            }
        }
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new {@link List} to display.
     */
    @SuppressWarnings("WeakerAccess")
    open fun updateData(data: List<ITEM>) {
        this.itemList = data
        notifyDataSetChanged()
    }

    fun add(item: ITEM) {
        itemList.toMutableList().add(item)
        notifyItemInserted(itemList.size)
    }

    fun remove(position: Int) {
        itemList.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }

    final override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        onViewRecycled(holder.itemView)
    }

    open fun onViewRecycled(itemView: View) {
    }

    open fun onItemClick(itemView: View, position: Int) {
    }

    open fun View.bind(item: ITEM, position: Int) {
    }

    open fun View.bindHeader() {}

    open fun View.bindFooter() {}

    open fun View.bindEmpty() {}

    fun forEach(block: (ITEM) -> Unit) {
        itemList.forEach { item -> block(item) }
    }

    private fun viewCountWhenEmptyItems(): Int {
        var viewCount = 0
        if (headerVisible()) {
            viewCount += 1
        }
        if (emptyVisible()) {
            viewCount += emptyCount
        }
        return viewCount
    }

    private fun viewMoreCount(): Int {
        var viewCount = 0
        if (headerVisible()) {
            viewCount += 1
        }
        if (footerVisible()) {
            viewCount += 1
        }
        return viewCount
    }

    private fun offset() = if (headerVisible()) 1 else 0

    private fun footerVisible() = (itemList.size >= numberPerPage && footerLayoutResId > 0)

    private fun headerVisible() = headerLayoutResId > 0

    private fun emptyVisible() = (emptyLayoutResId > 0 && itemList.isEmpty())

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val TYPE_NONE = -1
        const val TYPE_HEADER = 0
        const val TYPE_FOOTER = 1
        const val TYPE_ITEM_LOADED = 2
        const val TYPE_EMPTY = 3

        const val ITEM_TOP = 0
    }
}