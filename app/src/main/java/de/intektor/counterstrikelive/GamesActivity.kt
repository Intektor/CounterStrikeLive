package de.intektor.counterstrikelive

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import de.intektor.counterstrikelive.games_activity.GameAdapter
import de.intektor.counterstrikelive.games_activity.GameItem
import de.intektor.counterstrikelive.games_activity.HLTVMatches
import de.intektor.counterstrikelive.games_activity.loadHLTVMatches
import kotlinx.android.synthetic.main.activity_overview.*
import java.util.*

class GamesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        activityOverviewSwipeRefresh.setOnRefreshListener {
            RefreshTask().execute(this)
        }

        activityOverviewSwipeRefresh.isRefreshing = true

        val adapter = GameAdapter(this, mutableListOf(), { gameItem ->
            val intent = Intent(this, MatchActivity::class.java)
            intent.putExtra(KEY_MATCH_ID, gameItem.match.matchID)
            startActivity(intent)
        })
        activityOverviewGameList.layoutManager = LinearLayoutManager(activityOverviewGameList.context)
        activityOverviewGameList.adapter = adapter

        RefreshTask().execute(this)
    }

    fun updateHLTVMatches(matches: HLTVMatches) {
        activityOverviewSwipeRefresh.isRefreshing = false
        val allMatches = matches.liveMatches + matches.upcomingMatches

        val calendar = Calendar.getInstance()

        val groupedByDate = allMatches.groupBy {
            if (it.time == -1L) return@groupBy -1
            calendar.time = Date(it.time)
            calendar.get(Calendar.DAY_OF_MONTH)
        }

        calendar.time = Date()
        val gamesToday = mutableListOf<GameItem>()
        gamesToday += ((groupedByDate[calendar.get(Calendar.DAY_OF_MONTH)]
                ?: mutableListOf()) + (groupedByDate[-1]
                ?: mutableListOf())).map { GameItem(it, GameRole.NOT_DEFINED, GameRole.NOT_DEFINED) }

        val adapter = activityOverviewGameList.adapter
        adapter as GameAdapter
        adapter.listContents.clear()
        adapter.listContents += gamesToday

        adapter.notifyDataSetChanged()
    }

    class RefreshTask : AsyncTask<GamesActivity, GamesActivity, Pair<GamesActivity, HLTVMatches>>() {

        override fun doInBackground(vararg params: GamesActivity?): Pair<GamesActivity, HLTVMatches> = Pair(params[0]!!, loadHLTVMatches())

        override fun onPostExecute(result: Pair<GamesActivity, HLTVMatches>) {
            super.onPostExecute(result)
            result.first.updateHLTVMatches(result.second)
        }

    }
}
