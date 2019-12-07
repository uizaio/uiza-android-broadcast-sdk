package io.uiza.samplelive

import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.uiza.core.models.CreateLiveEntityBody
import io.uiza.core.models.DeleteLiveEntityResponse
import io.uiza.core.models.LiveEntity
import io.uiza.core.utils.execSubscribe
import io.uiza.core.utils.getData
import io.uiza.extensions.lauchActivity
import io.uiza.extensions.setVertical
import kotlinx.android.synthetic.main.activity_live_list.*
import kotlinx.android.synthetic.main.dlg_create_live.view.*
import timber.log.Timber

class LiveListActivity : AppCompatActivity(), EntityAdapter.MoreActionListener,
    PopupMenu.OnMenuItemClickListener {

    private val compositeDisposable = CompositeDisposable()
    private var currentEntityId: String? = null
    private var adapter: EntityAdapter = EntityAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_list)
        progress_bar.visibility = View.GONE
        contentList.setVertical()
        adapter.listener = this
        contentList.adapter = adapter
        fb_btn.setOnClickListener { showCreateLiveDialog() }
        loadEntities()
    }

    private fun loadEntities() {
        progress_bar.visibility = View.VISIBLE
        compositeDisposable.add(
            (application as SampleLiveApplication).liveService.getEntities()
                .getData().execSubscribe(
                    Consumer { entities: List<LiveEntity>? ->
                        entities?.let {
                            adapter.setData(it)
                        }
                        progress_bar.visibility = View.GONE
                    },
                    Consumer { throwable ->
                        progress_bar.visibility = View.GONE
                        Timber.e(throwable)
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
            var isValid = true
            if (streamName.isBlank()) {
                view.stream_name.error = "Error"
                isValid = false
            }
            if (isValid) {
                // do something
                dialog.dismiss()
                createLive(streamName.toString())
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(v.context, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.del_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun onMoreClick(v: View, entityId: String) {
        currentEntityId = entityId
        showPopup(v)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.del_entity) {
            removeEntity()
            return true
        }
        return false
    }

    /// CALL API

    private fun createLive(streamName: String) {
        progress_bar.visibility = View.VISIBLE
        val body = CreateLiveEntityBody(
            streamName,
            "Demo of $streamName",
            SampleLiveApplication.REGION,
            SampleLiveApplication.APP_ID,
            SampleLiveApplication.USER_ID
        )
        val obs =
            (application as SampleLiveApplication).liveService.createEntity(body)
        obs.execSubscribe(Consumer { res: LiveEntity ->
            lauchActivity<CheckLiveActivity> {
                putExtra(CheckLiveActivity.EXTRA_ENTITY, res)
            }
        }, Consumer { throwable: Throwable ->
            Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun removeEntity() {
        currentEntityId?.let {
            val obs = (application as SampleLiveApplication).liveService.deleteEntity(it)
            obs.execSubscribe(
                Consumer { res: DeleteLiveEntityResponse ->
                    res.id?.let { entityId ->
                        if (res.deleted == true) {
                            adapter.getItem(entityId)?.let { entity ->
                                adapter.removeItem(entity)
                            }
                        }
                        Timber.d(res.toString())
                    }
                },
                Consumer { throwable ->
                    Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_SHORT).show()
                })
        }
    }

}
