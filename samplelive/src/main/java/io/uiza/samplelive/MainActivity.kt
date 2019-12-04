package io.uiza.samplelive

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.uiza.extensions.lauchActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.live_btn).setOnClickListener(this)
        findViewById<View>(R.id.setting_btn).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.live_btn -> {
                lauchActivity<LiveListActivity> {}
            }
            R.id.setting_btn -> {
                lauchActivity<SettingsActivity> {}
            }
            else -> {
            }
        }
    }
}
