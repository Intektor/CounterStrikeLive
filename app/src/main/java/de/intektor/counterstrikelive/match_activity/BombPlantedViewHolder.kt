package de.intektor.counterstrikelive.match_activity

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.intektor.counterstrikelive.R

/**
 * @author Intektor
 */
class BombPlantedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val planterLabel: TextView = view.findViewById(R.id.logBombPlantedPlanterLabel)
}