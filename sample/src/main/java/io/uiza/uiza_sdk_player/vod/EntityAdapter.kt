package io.uiza.uiza_sdk_player.vod

import android.view.View
import io.uiza.core.models.UizaEntity
import io.uiza.core.utils.UizaLog
import io.uiza.extensions.lauchActivity
import io.uiza.uiza_sdk_player.R
import io.uiza.uiza_sdk_player.custom.BaseAdapter
import io.uiza.uiza_sdk_player.live.CheckLiveActivity
import kotlinx.android.synthetic.main.row_entity.view.*

class EntityAdapter(var entities: List<UizaEntity>, var isVod: Boolean = true) :
    BaseAdapter<UizaEntity>(
        entities,
        R.layout.row_entity
    ) {


    override fun onItemClick(itemView: View, position: Int) {
        UizaLog.e("EntityAdapter", entities[position].toString())
        if (isVod) {
            itemView.context.lauchActivity<InfoActivity> {
                putExtra(InfoActivity.EXTRA_ENTITY, entities[position])
            }
        } else {
            itemView.context.lauchActivity<CheckLiveActivity> {
                putExtra(CheckLiveActivity.EXTRA_ENTITY, entities[position])
            }
        }

    }

    override fun View.bind(item: UizaEntity, position: Int) {
        title.text = item.name
        description.text = item.description
    }
}