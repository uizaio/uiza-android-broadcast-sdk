package io.uiza.uiza_sdk_player

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


    override fun onItemClick(itemView: View, position: Int) {
        getItem(position)?.let { entity ->
            itemView.context.lauchActivity<InfoActivity> {
                putExtra(InfoActivity.EXTRA_ENTITY, entity)
            }
        }

    }

    override fun View.bind(item: UizaLiveEntity, position: Int) {
        title.text = item.name
        description.text = item.description
    }
}