package io.uiza.samplelive

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.uiza.extensions.lauchActivity
import kotlinx.android.synthetic.main.dlg_create_live.view.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.live_btn).setOnClickListener(this)
        findViewById<View>(R.id.force_live_btn).setOnClickListener(this)
        findViewById<View>(R.id.setting_btn).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.live_btn -> {
                lauchActivity<LiveListActivity> {}
            }
            R.id.force_live_btn -> {
                showCreateLiveDialog()
            }
            R.id.setting_btn -> {
                lauchActivity<SettingsActivity> {}
            }
            else -> {
            }
        }
    }

    private fun showCreateLiveDialog() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("New livestream")

        // Seems ok to inflate view with null rootView
        val view = layoutInflater.inflate(R.layout.dlg_create_live, null)

        builder.setView(view)
        view.stream_name.hint = "http://your_endpoint"
        view.stream_name.setText("rtmp://35.240.155.117/transcode/live_OqHu8SLArw")
        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val yourEndpoint = view.stream_name.text ?: ""
            var isValid = true
            if (yourEndpoint.isBlank()) {
                view.stream_name.error = "Error"
                isValid = false
            }
            if (isValid) {
                // do something
                lauchActivity<UizaLiveActivity> {
                    putExtra(SampleLiveApplication.EXTRA_STREAM_ENDPOINT, yourEndpoint)
                }
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
