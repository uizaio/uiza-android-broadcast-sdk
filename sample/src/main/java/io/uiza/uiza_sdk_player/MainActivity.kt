package io.uiza.uiza_sdk_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.uiza.core.utils.UizaLog
import io.uiza.core.utils.execSubscribe
import io.uiza.extensions.setVertical
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contentList.setVertical()
        loadEntities()
    }

    private fun loadEntities() {
        compositeDisposable.add((application as SampleApplication).liveService.getEntities()
            .map { response -> response.entities }.execSubscribe(Consumer { entities ->
                entities?.let {
                    contentList.adapter = EntityAdapter(it)
                }
            }, Consumer { throwable ->
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
