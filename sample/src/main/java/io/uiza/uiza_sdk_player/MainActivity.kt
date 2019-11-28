package io.uiza.uiza_sdk_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.functions.Consumer
import io.uiza.core.utils.ObservableUtil
import io.uiza.core.utils.UizaLog
import io.uiza.extensions.setVertical
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contentList.setVertical()
        loadEntities()
    }

    private fun loadEntities() {
        ObservableUtil.subscribe((application as SampleApplication).liveService.getEntities()
            .map { response -> response.entities }, Consumer { entities ->
            entities?.let {
                contentList.adapter = EntityAdapter(it)
            }
        }, Consumer { throwable ->
            UizaLog.e("MainActivity", "error: " + throwable?.localizedMessage)
        })

    }
}
