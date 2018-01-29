package de.intektor.counterstrikelive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.intektor.counterstrikelive.match_activity.RoundLog
import de.intektor.counterstrikelive.match_activity.RoundLogAdapter
import kotlinx.android.synthetic.main.activity_match.*

class MatchActivity : AppCompatActivity() {

    private lateinit var adapter: RoundLogAdapter

    private lateinit var hltvMatchEventListener: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        val matchID = intent.getLongExtra(KEY_MATCH_ID, -1)

        adapter = RoundLogAdapter(this, mutableListOf(), { item ->
            item.expandView = !item.expandView
            adapter.notifyItemChanged(adapter.roundLog.indexOf(item))
        })

        activityMatchGameLog.layoutManager = LinearLayoutManager(this)
        activityMatchGameLog.adapter = adapter

        val imageView = ImageView(this)

        Picasso.with(this).load("https://static.hltv.org/images/scoreboardmaps/de_inferno.png").into(imageView, object : Callback {
            override fun onSuccess() {
                matchActivityParentLayout.background = imageView.drawable
            }

            override fun onError() {
            }

        })

        hltvMatchEventListener = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val event = intent.getParcelableExtra<Parcelable>(KEY_HLTV_MATCH_EVENT)
                when (event) {
                    is HLTVGameLogService.RoundStart -> {
                        val roundLog = RoundLog(0, 0, 130, false, false, false, false,
                                false, false, false, false, false, adapter.roundLog.size + 1, mutableListOf(), false)

                        adapter.roundLog.add(0, roundLog)
                        adapter.notifyItemInserted(0)
                    }
                    is HLTVGameLogService.RoundEnd -> {
                        val roundLog = adapter.roundLog.firstOrNull() ?: return
                        when (event.winType) {
                            "CTs-Win" -> {
                                roundLog.tsEliminated = true
                            }
                            "Terrorists_Win" -> {
                                roundLog.ctsEliminated = true
                            }
                            "Target_Bombed" -> {
                                roundLog.bombExploded = true
                            }
                            "Bomb_Defused" -> {
                                roundLog.bombDefused = true
                            }
                            "Round_Draw" -> {
                                roundLog.roundDrawn = true
                            }
                            else -> {
                                roundLog.timeOver = true
                            }
                        }
                        when (GameRole.forHLTVName(event.winner)) {
                            GameRole.COUNTER_TERRORIST -> {
                                roundLog.ctWin = true
                            }
                            GameRole.TERRORIST -> {
                                roundLog.tWin = true
                            }
                            GameRole.NOT_DEFINED -> {

                            }
                        }
                        adapter.notifyItemChanged(0)
                    }
                    is HLTVGameLogService.Kill -> {
                        val roundLog = adapter.roundLog.firstOrNull()
                        if (roundLog != null) {
                            roundLog.roundLog += event

                            when (event.victimSide) {
                                GameRole.TERRORIST -> {
                                    roundLog.deadTs++
                                }
                                GameRole.COUNTER_TERRORIST -> {
                                    roundLog.deadCTs++
                                }
                                GameRole.NOT_DEFINED -> {

                                }
                            }

                            adapter.notifyItemChanged(0)
                        }
                    }
                }
            }
        }

        val serviceIntent = Intent(this, HLTVGameLogService::class.java)
        serviceIntent.putExtra(KEY_MATCH_ID, matchID)
        startService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(hltvMatchEventListener, IntentFilter(ACTION_HLTV_MATCH_EVENT))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(hltvMatchEventListener)
    }
}
