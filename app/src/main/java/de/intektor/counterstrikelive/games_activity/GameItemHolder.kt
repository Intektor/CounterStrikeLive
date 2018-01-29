package de.intektor.counterstrikelive.games_activity

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.intektor.counterstrikelive.GameRole
import de.intektor.counterstrikelive.R

/**
 * @author Intektor
 */
class GameItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    val team1Label: TextView = view.findViewById(R.id.gameItemTeam1Label)
    val team2Label: TextView = view.findViewById(R.id.gameItemTeam2Label)

    val team1Symbol: ImageView = view.findViewById(R.id.gameItemTeam1Symbol)
    val team2Symbol: ImageView = view.findViewById(R.id.gameItemTeam2Symbol)

    val team1Score: TextView = view.findViewById(R.id.gameItemScoreTeam1)
    val team2Score: TextView = view.findViewById(R.id.gameItemScoreTeam2)

    val mapName: TextView = view.findViewById(R.id.gameItemMapLabel)

    fun showLiveScore(show: Boolean) {
        val newState = if (show) View.VISIBLE else View.GONE
        team1Score.visibility = newState
        team2Score.visibility = newState

        mapName.visibility = newState
    }

    fun setRoleTeam1(role: GameRole) {
        setRoleToView(role, team1Label, team1Score)
    }

    fun setRoleTeam2(role: GameRole) {
        setRoleToView(role, team2Label, team2Score)
    }

    private fun setRoleToView(role: GameRole, teamLabel: TextView, teamScore: TextView) {
        val color: Int = when (role) {
            GameRole.TERRORIST -> R.color.color_terrorist
            GameRole.COUNTER_TERRORIST -> R.color.color_counter_terrorists
            GameRole.NOT_DEFINED -> R.color.color_role_not_defined
        }

        teamLabel.setTextColor(color)
        teamScore.setTextColor(color)
    }
}