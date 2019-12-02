package io.uiza.uiza_sdk_player.vod

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.uiza.core.utils.UizaLog
import io.uiza.core.utils.execSubscribe
import io.uiza.extensions.setVertical
import io.uiza.uiza_sdk_player.R
import io.uiza.uiza_sdk_player.SampleApplication
import kotlinx.android.synthetic.main.activity_vod_list.*


class VODListActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vod_list)
        contentList.setVertical()
        loadEntities()
    }

    private fun loadEntities() {
        progress_bar.visibility = View.VISIBLE
        compositeDisposable.add((application as SampleApplication).liveService.getEntities()
            .map { response -> response.entities }.execSubscribe(Consumer { entities ->
                entities?.let {
                    contentList.adapter =
                        EntityAdapter(it)
                }
                progress_bar.visibility = View.GONE
            }, Consumer { throwable ->
                progress_bar.visibility = View.GONE
                UizaLog.e("MainActivity", "error: " + throwable?.localizedMessage)
            })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}
