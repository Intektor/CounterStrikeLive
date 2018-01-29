package de.intektor.counterstrikelive.match_activity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import de.intektor.counterstrikelive.GameRole
import de.intektor.counterstrikelive.HLTVGameLogService
import de.intektor.counterstrikelive.MatchActivity
import de.intektor.counterstrikelive.R

/**
 * @author Intektor
 */
class MatchLogAdapter(val activity: MatchActivity, val roundLog: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val KILL_VIEW = 0
        private const val BOMB_PLANTED = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            KILL_VIEW -> KillViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_kill, parent, false))
            BOMB_PLANTED -> BombPlantedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_bomb_planted, parent, false))
            else -> {
                throw RuntimeException()
            }
        }
    }

    override fun getItemCount(): Int = roundLog.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = roundLog[position]
        when (holder) {
            is KillViewHolder -> {
                item as HLTVGameLogService.Kill
                holder.killerLabel.text = item.killerName
                holder.victimLabel.text = item.victimName

                val killerColor = when (item.killerSide) {
                    GameRole.TERRORIST -> R.color.color_terrorist
                    GameRole.COUNTER_TERRORIST -> R.color.color_counter_terrorists
                    GameRole.NOT_DEFINED -> android.R.color.white
                }

                holder.killerLabel.setTextColor(activity.resources.getColor(killerColor))
                val victimColor = when (item.victimSide) {
                    GameRole.TERRORIST -> R.color.color_terrorist
                    GameRole.COUNTER_TERRORIST -> R.color.color_counter_terrorists
                    GameRole.NOT_DEFINED -> android.R.color.white
                }
                holder.victimLabel.setTextColor(activity.resources.getColor(victimColor))

                Picasso.with(activity).load("https://www.hltv.org/img/static/scoreboard/weapons/${item.weapon}.png").into(holder.weaponView)
            }
            is BombPlantedViewHolder -> {
                item as HLTVGameLogService.BombPlanted
                holder.planterLabel.text = item.playerName
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = roundLog[position]
        when (item) {
            is HLTVGameLogService.Kill -> KILL_VIEW
            is HLTVGameLogService.BombPlanted -> BOMB_PLANTED
        }
        return 0
    }

}