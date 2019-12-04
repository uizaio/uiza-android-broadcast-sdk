package io.uiza.samplelive

import android.view.View
import io.uiza.core.models.UizaLiveEntity
import io.uiza.extensions.BaseAdapter
import io.uiza.extensions.lauchActivity
import kotlinx.android.synthetic.main.row_entity.view.*


class EntityAdapter(var entities: List<UizaLiveEntity> = emptyList()) :
    BaseAdapter<UizaLiveEntity>(
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

    fun getItem(id: String): UizaLiveEntity? {
        return getData().firstOrNull { item -> item.id == id }
    }

    override fun View.bind(item: UizaLiveEntity, position: Int) {
        title.text = item.name
        description.text = item.description
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