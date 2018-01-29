package de.intektor.counterstrikelive.match_activity

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.intektor.counterstrikelive.R

/**
 * @author Intektor
 */
class KillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val killerLabel: TextView = view.findViewById(R.id.logKillKillerLabel)
    val victimLabel: TextView = view.findViewById(R.id.logKillVictimLabel)
    val weaponView: ImageView = view.findViewById(R.id.logKillWeaponView)
}