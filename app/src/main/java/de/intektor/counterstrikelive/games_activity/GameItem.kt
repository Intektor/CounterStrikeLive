package de.intektor.counterstrikelive.games_activity

import de.intektor.counterstrikelive.GameRole

/**
 * @author Intektor
 */
data class GameItem(val match: Match, var roleTeam1: GameRole, var roleTeam2: GameRole)