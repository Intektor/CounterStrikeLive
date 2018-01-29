package de.intektor.counterstrikelive.match_activity

/**
 * @author Intektor
 */
class RoundLog(var deadTs: Int, var deadCTs: Int, var time: Int, var bombPlanted: Boolean, var bombDefused: Boolean,
               var tsEliminated: Boolean, var ctsEliminated: Boolean, var timeOver: Boolean, var bombExploded: Boolean,
               var expandView: Boolean, var tWin: Boolean, var ctWin: Boolean, val roundNumber: Int, val roundLog: MutableList<Any>,
               var roundDrawn: Boolean)