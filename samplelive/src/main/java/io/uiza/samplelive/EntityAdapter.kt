package io.uiza.samplelive

import android.view.View
import io.uiza.core.models.v5.LiveEntity
import io.uiza.extensions.BaseAdapter
import io.uiza.extensions.lauchActivity
import kotlinx.android.synthetic.main.row_entity.view.*


class EntityAdapter(var entities: List<LiveEntity> = emptyList()) :
    BaseAdapter<LiveEntity>(
        entities,
        R.layout.row_entity,
        emptyLayoutResId = R.layout.layout_empty
    ) {

    var listener: MoreActionListener? = null

    override fun onItemClick(itemView: View, position: Int) {
        getItem(position)?.let { entity ->
            itemView.context.lauchActivity<CheckLiveActivity> {
                putExtra(CheckLiveActivity.EXTRA_ENTITY, entity)
            }
        }


    }

    fun getItem(id: String): LiveEntity? {
        return getData().firstOrNull { item -> item.id == id }
    }

    override fun View.bind(item: LiveEntity, position: Int) {
        title.text = item.name
        description.text = item.status
        listener?.let { l ->
            action_button.setOnClickListener {
                l.onMoreClick(it, item.id)
            }
        }
    }

    interface MoreActionListener {
        fun onMoreClick(v: View, entityId: String)
    }
}