package de.intektor.counterstrikelive.games_activity

import org.jsoup.Jsoup

/**
 * @author Intektor
 */
fun loadHLTVMatches(): HLTVMatches {
    val doc = Jsoup.connect("https://www.hltv.org/matches").userAgent("Chrome/23.0.1271.95").get()

    val liveMatchesList = mutableListOf<LiveMatch>()

    val liveMatches = doc.getElementsByClass("live-matches")
    if (liveMatches.isNotEmpty()) {
        for (actLiveMatch in liveMatches[0].getElementsByClass("standard-box")) {
            val liveMatch = actLiveMatch.getElementsByAttribute("data-livescore-match")[0]

            val event = actLiveMatch.getElementsByClass("live-match-header")[0]

            val eventName = event.getElementsByClass("event-name")[0].text()
            val eventLogo = event.getElementsByClass("event-logo").attr("src")

            val bestof = liveMatch.getElementsByClass("bestof")[0].text()
            val maps = liveMatch.getElementsByClass("map").filter { it.className() == "map" }.map { it.text() }

            val teams = liveMatch.child(0).children().filter { it.allElements.any { it.hasClass("teams") } }

            val finalTeams = mutableListOf<Pair<Team, Map<Int, Int?>>>()

            for (team in teams) {
                val teamName = team.getElementsByClass("team-name")[0].text()
                val teamLogo = team.getElementsByClass("logo").attr("src")
                val map = hashMapOf<Int, Int?>()
                var teamId: Long = -1
                for (score in team.getElementsByAttribute("data-livescore-map")) {
                    val mapID = score.attr("data-livescore-map")
                    val mapScore = score.text()
                    map[mapID.toInt()] = mapScore.toIntOrNull()

                    teamId = score.attr("data-livescore-team").toLong()

                }

                finalTeams += Pair(Team(teamName, teamLogo, teamId), map)
            }

            val mapsMap = hashMapOf<Int, String>()

            for ((index, map) in maps.withIndex()) {
                mapsMap[index] = map
            }

            val matchId = liveMatch.getElementsByAttribute("data-livescore-match")[0].attr("data-livescore-match")

            liveMatchesList += LiveMatch(finalTeams[0].first, finalTeams[1].first, eventName, eventLogo, matchId.toLong(), bestof, finalTeams[0].second, finalTeams[1].second, mapsMap)
        }
    }

    val matchDays = doc.getElementsByClass("match-day")
    val matches = mutableListOf<Match>()
    for (day in matchDays) {
        val dayMatches = day.getElementsByAttribute("data-zonedgrouping-entry-unix")
        for (match in dayMatches) {
            try {
                val time = match.attr("data-zonedgrouping-entry-unix")
                val teams = match.getElementsByClass("team-cell").map { team ->
                    val teamName = team.getElementsByClass("team")[0].text()
                    val img = team.getElementsByClass("logo").attr("src")
                    val lastChar = img.lastIndexOf("/")
                    val teamId = img.substring(lastChar + 1)
                    Team(teamName, img, teamId.toLong())
                }
                val event = match.getElementsByClass("event")
                if (event.isEmpty()) continue
                val eventName = event[0].getElementsByClass("event-name").text()
                val eventImg = event[0].getElementsByClass("event-logo").attr("src")

                val matchIdP = match.attr("href").substring("/matches/".length)
                val cut = matchIdP.indexOf('/')
                val matchId = matchIdP.substring(0, cut)

                val gameType = match.getElementsByClass("map-text")[0].text()

                matches += Match(time.toLong(), teams[0], teams[1], eventName, eventImg, matchId.toLong(), gameType)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
    return HLTVMatches(liveMatchesList, matches)
}

open class Match(val time: Long, val team1: Team, val team2: Team, val eventName: String, val eventImgURL: String, val matchID: Long, val gameType: String)

class LiveMatch(team1: Team, team2: Team, eventName: String, eventImgURL: String, matchID: Long,
                gameType: String, val scoreTeam1: Map<Int, Int?>, val scoreTeam2: Map<Int, Int?>, val maps: Map<Int, String>)
    : Match(-1, team1, team2, eventName, eventImgURL, matchID, gameType)

data class Team(val teamName: String, val teamImgURL: String, val teamID: Long)

data class HLTVMatches(val liveMatches: List<LiveMatch>, val upcomingMatches: List<Match>)