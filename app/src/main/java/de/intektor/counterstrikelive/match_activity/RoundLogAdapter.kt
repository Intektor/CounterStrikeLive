package de.intektor.counterstrikelive.match_activity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.intektor.counterstrikelive.MatchActivity
import de.intektor.counterstrikelive.R

/**
 * @author Intektor
 */
class RoundLogAdapter(private val activity: MatchActivity, val roundLog: MutableList<RoundLog>, private val onClickListener: (RoundLog) -> (Unit)) : RecyclerView.Adapter<RoundLogViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.round_list_item, parent, false)
        return RoundLogViewHolder(view)
    }

    override fun getItemCount(): Int = roundLog.size

    override fun onBindViewHolder(holder: RoundLogViewHolder, position: Int) {
        val item = roundLog[position]
        for (i in 0 until 5) {
            holder.tImages[i].setImageResource(R.drawable.t_alive)
        }
        for (i in 0 until 5) {
            holder.ctImages[i].setImageResource(R.drawable.ct_alive)
        }
        for (i in 0 until Math.min(item.deadTs, 5)) {
            holder.tImages[i].setImageResource(R.drawable.t_death)
        }
        for (i in 0 until Math.min(item.deadCTs, 5)) {
            holder.ctImages[i].setImageResource(R.drawable.ct_death)
        }

        val m = Math.floor(item.time / 60.0).toInt()
        val s = item.time % 60

        holder.roundTimer.text = String.format("%02d:%02d", m, s)

        when {
            item.bombPlanted -> when {
                item.bombDefused -> holder.roundItemMatchImage.setImageResource(R.drawable.defused)
                item.bombExploded -> holder.roundItemMatchImage.setImageResource(R.drawable.bomb_exploded)
                else -> holder.roundItemMatchImage.setImageResource(R.drawable.bomb)
            }
            item.ctsEliminated -> holder.roundItemMatchImage.setImageResource(R.drawable.ct_death)
            item.tsEliminated -> holder.roundItemMatchImage.setImageResource(R.drawable.t_death)
            item.timeOver -> holder.roundItemMatchImage.setImageResource(R.drawable.time_over)
        }

        when {
            item.tWin -> {
                holder.winLabel.setText(R.string.match_activity_game_log_t_win)
                holder.winLabel.setTextColor(activity.resources.getColor(R.color.color_terrorist))
            }
            item.ctWin -> {
                holder.winLabel.setText(R.string.match_activity_game_log_ct_win)
                holder.winLabel.setTextColor(activity.resources.getColor(R.color.color_counter_terrorists))
            }
            else -> {
                holder.winLabel.setText(R.string.match_activity_game_log_round_ongoing)
                holder.winLabel.setTextColor(activity.resources.getColor(R.color.colorAccent))
            }
        }

        holder.roundLabel.text = activity.getString(R.string.round_item_round_label, item.roundNumber)

        holder.roundLogView.visibility = if (item.expandView) View.VISIBLE else View.GONE

        val adapter = MatchLogAdapter(activity, item.roundLog)

        holder.roundLogView.adapter = adapter
        holder.roundLogView.layoutManager = LinearLayoutManager(holder.itemView.context)
        adapter.notifyDataSetChanged()

        holder.itemView.setOnClickListener {
            onClickListener.invoke(item)
        }
    }

}