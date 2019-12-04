package io.uiza.samplelive

import android.view.View
import io.uiza.core.models.UizaEntity
import io.uiza.core.utils.UizaLog
import io.uiza.extensions.BaseAdapter
import io.uiza.extensions.lauchActivity
import kotlinx.android.synthetic.main.row_entity.view.*

class EntityAdapter(var entities: List<UizaEntity>) :
    BaseAdapter<UizaEntity>(
        entities,
        R.layout.row_entity
    ) {

    override fun onItemClick(itemView: View, position: Int) {
        UizaLog.e("EntityAdapter", entities[position].toString())
        itemView.context.lauchActivity<CheckLiveActivity> {
            putExtra(CheckLiveActivity.EXTRA_ENTITY, entities[position])
        }

    }

    override fun View.bind(item: UizaEntity, position: Int) {
        title.text = item.name
        description.text = item.description
    }
}