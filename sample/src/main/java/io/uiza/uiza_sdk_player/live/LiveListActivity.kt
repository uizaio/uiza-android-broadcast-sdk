package io.uiza.uiza_sdk_player.live

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.uiza.core.models.CreateEntityBody
import io.uiza.core.models.UizaEntity
import io.uiza.core.utils.UizaLog
import io.uiza.core.utils.execSubscribe
import io.uiza.extensions.lauchActivity
import io.uiza.extensions.setVertical
import io.uiza.uiza_sdk_player.R
import io.uiza.uiza_sdk_player.SampleApplication
import io.uiza.uiza_sdk_player.vod.EntityAdapter
import kotlinx.android.synthetic.main.activity_vod_list.*
import kotlinx.android.synthetic.main.dlg_create_live.view.*

class LiveListActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vod_list)
        progress_bar.visibility = View.GONE
        contentList.setVertical()
        fb_btn.setOnClickListener { showCreateLiveDialog() }
        loadEntities()
    }

    private fun loadEntities() {
        progress_bar.visibility = View.VISIBLE
        compositeDisposable.add(
            (application as SampleApplication).liveService.getEntities()
                .map { response -> response.entities?.filter { entity -> entity.needGetInfo() } }.execSubscribe(
                    Consumer { entities ->
                        entities?.let {
                            contentList.adapter =
                                EntityAdapter(it, false)
                        }
                        progress_bar.visibility = View.GONE
                    },
                    Consumer { throwable ->
                        progress_bar.visibility = View.GONE
                        UizaLog.e("MainActivity", "error: " + throwable?.localizedMessage)
                    })
        )
    }

    private fun showCreateLiveDialog() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("New livestream")

        // Seems ok to inflate view with null rootView
        val view = layoutInflater.inflate(R.layout.dlg_create_live, null)

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val streamName = view.stream_name.text ?: ""
            val description = view.stream_desc.text ?: ""
            var isValid = true
            if (streamName.isBlank()) {
                view.stream_name.error = "Error"
                isValid = false
            }
            if (description.isBlank()) {
                view.stream_desc.error = "Error"
                isValid = false
            }
            if (isValid) {
                // do something
                dialog.dismiss()
                createLive(streamName.toString(), description.toString())
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun createLive(streamName: String, description: String) {
        progress_bar.visibility = View.VISIBLE
        val body = CreateEntityBody(
            streamName,
            description,
            SampleApplication.REGION,
            SampleApplication.APP_ID,
            SampleApplication.USER_ID
        )
        val obs =
            (application as SampleApplication).liveService.createEntity(body)
        obs.execSubscribe(Consumer { res: UizaEntity ->
            lauchActivity<CheckLiveActivity> {
                putExtra(CheckLiveActivity.EXTRA_ENTITY, res)
            }
        }, Consumer { throwable: Throwable ->
            Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}
