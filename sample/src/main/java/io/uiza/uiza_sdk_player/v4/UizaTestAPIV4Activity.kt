package io.uiza.uiza_sdk_player.v4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.uiza.core.api.UizaV3Service
import io.uiza.uiza_sdk_player.R
import io.uiza.uiza_sdk_player.SampleApplication

class UizaTestAPIV4Activity : AppCompatActivity() {

    var v3Service: UizaV3Service? = null
    var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_test)
        v3Service = (application as SampleApplication).v3Service
        compositeDisposable = CompositeDisposable()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (compositeDisposable?.isDisposed == false) {
            compositeDisposable?.dispose()
        }
    }
}
