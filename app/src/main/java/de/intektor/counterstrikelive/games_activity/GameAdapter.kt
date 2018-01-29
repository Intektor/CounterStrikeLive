package de.intektor.counterstrikelive.games_activity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import de.intektor.counterstrikelive.GamesActivity
import de.intektor.counterstrikelive.R

/**
 * @author Intektor
 */
class GameAdapter(private val activity: GamesActivity, val listContents: MutableList<GameItem>, private val onClickListener: (GameItem) -> (Unit)) : RecyclerView.Adapter<GameItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return GameItemHolder(view)
    }

    override fun getItemCount(): Int = listContents.size

    override fun onBindViewHolder(holder: GameItemHolder, position: Int) {
        val item = listContents[position]
        holder.team1Label.text = item.match.team1.teamName
        holder.team2Label.text = item.match.team2.teamName

        holder.setRoleTeam1(item.roleTeam1)
        holder.setRoleTeam2(item.roleTeam2)

        Picasso.with(activity).load(item.match.team1.teamImgURL).into(holder.team1Symbol)
        Picasso.with(activity).load(item.match.team2.teamImgURL).into(holder.team2Symbol)

        holder.showLiveScore(item.match.time == -1L)

        holder.itemView.setOnClickListener {
            onClickListener.invoke(item)
        }
    }
}