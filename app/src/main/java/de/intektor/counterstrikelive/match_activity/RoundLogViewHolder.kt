package de.intektor.counterstrikelive.match_activity

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.intektor.counterstrikelive.R

/**
 * @author Intektor
 */
class RoundLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val winLabel: TextView = view.findViewById(R.id.roundItemWinLabel)
    val roundLabel: TextView = view.findViewById(R.id.roundItemRoundLabel)
    val roundItemMatchImage: ImageView = view.findViewById(R.id.roundItemMatchImage)
    val ctImages: Array<ImageView> = arrayOf(
            view.findViewById(R.id.roundItemCT1),
            view.findViewById(R.id.roundItemCT2),
            view.findViewById(R.id.roundItemCT3),
            view.findViewById(R.id.roundItemCT4),
            view.findViewById(R.id.roundItemCT5)
    )
    val tImages: Array<ImageView> = arrayOf(
            view.findViewById(R.id.roundItemT1),
            view.findViewById(R.id.roundItemT2),
            view.findViewById(R.id.roundItemT3),
            view.findViewById(R.id.roundItemT4),
            view.findViewById(R.id.roundItemT5)
    )
    val roundTimer: TextView = view.findViewById(R.id.roundItemTimer)
    val roundLogView: RecyclerView = view.findViewById(R.id.roundItemLogList)
}