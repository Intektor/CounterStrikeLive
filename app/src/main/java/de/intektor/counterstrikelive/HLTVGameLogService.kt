package de.intektor.counterstrikelive

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import org.json.JSONObject

class HLTVGameLogService : Service() {

    private val gameMap: MutableMap<Long, HLTVThread> = hashMapOf()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val senderKey = intent.getStringExtra(KEY_SENDER_KEY)
                val responseIntent = Intent(ACTION_GET_RUNNING_MATCHES_RESPONSE)
                responseIntent.putExtra(KEY_SENDER_KEY, senderKey)
                responseIntent.putExtra(KEY_MATCH_IDS, gameMap.keys.toLongArray())
                sendBroadcast(responseIntent)
            }
        }, IntentFilter(ACTION_GET_RUNNING_MATCHES))
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val matchId = intent.getLongExtra(KEY_MATCH_ID, -1)

        if (gameMap.containsKey(matchId)) {
            val t = gameMap[matchId]!!
            t.socket.disconnect()
            t.socket.close()
            t.socket.off()
        }
        val thread = HLTVThread(matchId, this)
        gameMap[matchId] = thread
        thread.start()

        return super.onStartCommand(intent, flags, startId)
    }

    class HLTVThread(private val matchId: Long, private val context: Context) : Thread() {

        val socket = IO.socket("https://scorebot-secure.hltv.org")

        override fun run() {
            socket
                    .on(Socket.EVENT_CONNECT, { data ->
                        socket.emit("connected")
                        socket.emit("readyForMatch", Gson().toJson(ListID(listId = matchId)))
                    })
                    .on("log", { data ->
                        handleLog(data)
                    })
                    .on("scoreboard", { data ->

                    })
                    .on("time", { data ->

                    })
            socket.connect()
        }

        private fun handleLog(data: Array<out Any>) {
            val jsonObject = JSONObject(data[0] as String)
            val logArray = jsonObject.getJSONArray("log")
            for (i in (0 until logArray.length()).reversed()) {
                val logObject = logArray.getJSONObject(i)
                val key = logObject.names()[0] as String
                val o = logObject.getJSONObject(key)
                when (key) {
                    "Kill" -> {
                        val weapon = o.getString("weapon")
                        val headshot = o.getBoolean("headShot")
                        val killerNick = o.getString("killerNick")
                        val victimName = o.getString("victimName")
                        val killerSide = o.getString("killerSide")
                        val victimSide = o.getString("victimSide")
                        val killerName = o.getString("killerName")
                        val victimNick = o.getString("victimNick")
                        val kill = Kill(weapon, headshot, killerNick, victimNick, killerName, victimName, GameRole.forHLTVName(killerSide), GameRole.forHLTVName(victimSide))
                        sendEvent(kill)
                    }
                    "Assist" -> {
                        println()
                    }
                    "BombPlanted" -> {
                        val playerName = o.getString("playerName")
                        val tPlayers = o.getString("tPlayers")
                        val playerNick = o.getString("playerNick")
                        val ctPlayer = o.getString("ctPlayers")
                        val bombPlanted = BombPlanted(playerName, playerNick, tPlayers, ctPlayer)
                        sendEvent(bombPlanted)
                    }
                    "PlayerJoin" -> {
                        val playerName = o.getString("playerName")
                        val playerNick = o.getString("playerNick")
                        val playerJoin = PlayerJoin(playerName, playerNick)
                        sendEvent(playerJoin)
                    }
                    "Suicide" -> {
                        val weapon = o.getString("weapon")
                        val side = o.getString("side")
                        val playerName = o.getString("playerName")
                        val playerNick = o.getString("playerNick")
                        val suicide = Suicide(weapon, side, playerName, playerNick)
                        sendEvent(suicide)
                    }
                    "RoundStart" -> {
                        val roundStart = RoundStart()
                        sendEvent(roundStart)
                    }
                    "MatchStarted" -> {
                        val map = o.getString("map")
                        val matchStarted = MatchStarted(map)
                        sendEvent(matchStarted)
                    }
                    "RoundEnd" -> {
                        val winner = o.getString("winner")
                        val counterTerroristScore = o.getString("counterTerroristScore")
                        val terroristScore = o.getString("terroristScore")
                        val winType = o.getString("winType")
                        val roundEnd = RoundEnd(winner, counterTerroristScore, terroristScore, winType)
                        sendEvent(roundEnd)
                    }
                    "Restart" -> {
                        val restart = Restart()
                        sendEvent(restart)
                    }
                    "PlayerQuit" -> {
                        val playerSide = o.getString("playerSide")
                        val playerName = o.getString("playerName")
                        val playerNick = o.getString("playerNick")
                        val playerQuit = PlayerQuit(playerSide, playerName, playerNick)
                        sendEvent(playerQuit)
                    }
                    "BombDefused" -> {
                        val playerName = o.getString("playerName")
                        val playerNick = o.getString("playerNick")
                        val bombDefused = BombDefused(playerName, playerNick)
                        sendEvent(bombDefused)
                    }
                    "MapChange" -> {
                        val map = o.getString("map")
                        val mapChange = MapChange(map)
                        sendEvent(mapChange)
                    }
                    else -> {
                        Log.e("ERROR", "Unknown event received: $key")
                    }
                }
            }
        }

        private fun sendEvent(event: Parcelable) {
            val eventIntent = Intent(ACTION_HLTV_MATCH_EVENT)
            eventIntent.putExtra(KEY_MATCH_ID, matchId)
            eventIntent.putExtra(KEY_HLTV_MATCH_EVENT, event)
            context.sendBroadcast(eventIntent)
        }
    }

    private data class ListID(val token: String = "", val listId: Long)

    data class RoundEnd(val winner: String, val counterTerroristScore: String, val terroristScore: String, val winType: String) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(winner)
            parcel.writeString(counterTerroristScore)
            parcel.writeString(terroristScore)
            parcel.writeString(winType)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<RoundEnd> {
            override fun createFromParcel(parcel: Parcel): RoundEnd = RoundEnd(parcel)

            override fun newArray(size: Int): Array<RoundEnd?> = arrayOfNulls(size)
        }
    }

    data class Kill(val weapon: String, val headshot: Boolean, val killerNick: String, val victimNick: String,
                    val killerName: String, val victimName: String, val killerSide: GameRole, val victimSide: GameRole) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readByte() != 0.toByte(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                GameRole.values()[parcel.readInt()],
                GameRole.values()[parcel.readInt()]
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(weapon)
            parcel.writeByte(if (headshot) 1 else 0)
            parcel.writeString(killerNick)
            parcel.writeString(victimNick)
            parcel.writeString(killerName)
            parcel.writeString(victimName)
            parcel.writeInt(killerSide.ordinal)
            parcel.writeInt(victimSide.ordinal)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Kill> {
            override fun createFromParcel(parcel: Parcel): Kill = Kill(parcel)

            override fun newArray(size: Int): Array<Kill?> = arrayOfNulls(size)
        }
    }

    data class PlayerJoin(val playerName: String, val playerNick: String) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(playerName)
            parcel.writeString(playerNick)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<PlayerJoin> {
            override fun createFromParcel(parcel: Parcel): PlayerJoin = PlayerJoin(parcel)

            override fun newArray(size: Int): Array<PlayerJoin?> = arrayOfNulls(size)
        }
    }

    data class Suicide(val weapon: String, val side: String, val playerName: String, val playerNick: String) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(weapon)
            parcel.writeString(side)
            parcel.writeString(playerName)
            parcel.writeString(playerNick)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Suicide> {
            override fun createFromParcel(parcel: Parcel): Suicide = Suicide(parcel)

            override fun newArray(size: Int): Array<Suicide?> = arrayOfNulls(size)
        }
    }

    data class MatchStarted(val map: String) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(map)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<MatchStarted> {
            override fun createFromParcel(parcel: Parcel): MatchStarted = MatchStarted(parcel)

            override fun newArray(size: Int): Array<MatchStarted?> = arrayOfNulls(size)
        }
    }

    data class PlayerQuit(val playerSide: String, val playerName: String, val playerNick: String) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(playerSide)
            parcel.writeString(playerName)
            parcel.writeString(playerNick)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<PlayerQuit> {
            override fun createFromParcel(parcel: Parcel): PlayerQuit = PlayerQuit(parcel)

            override fun newArray(size: Int): Array<PlayerQuit?> = arrayOfNulls(size)
        }
    }

    data class BombDefused(val playerName: String, val playerNick: String) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(playerName)
            parcel.writeString(playerNick)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<BombDefused> {
            override fun createFromParcel(parcel: Parcel): BombDefused = BombDefused(parcel)

            override fun newArray(size: Int): Array<BombDefused?> = arrayOfNulls(size)
        }
    }

    data class BombPlanted(val playerName: String, val playerNick: String, val tPlayers: String, val ctPlayers: String) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(playerName)
            parcel.writeString(playerNick)
            parcel.writeString(tPlayers)
            parcel.writeString(ctPlayers)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<BombPlanted> {
            override fun createFromParcel(parcel: Parcel): BombPlanted = BombPlanted(parcel)

            override fun newArray(size: Int): Array<BombPlanted?> = arrayOfNulls(size)
        }
    }

    class RoundStart() : Parcelable {
        constructor(parcel: Parcel) : this()

        override fun writeToParcel(parcel: Parcel, flags: Int) {

        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<RoundStart> {
            override fun createFromParcel(parcel: Parcel): RoundStart = RoundStart(parcel)

            override fun newArray(size: Int): Array<RoundStart?> = arrayOfNulls(size)
        }
    }

    class Restart() : Parcelable {
        constructor(parcel: Parcel) : this()

        override fun writeToParcel(parcel: Parcel, flags: Int) {

        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Restart> {
            override fun createFromParcel(parcel: Parcel): Restart = Restart(parcel)

            override fun newArray(size: Int): Array<Restart?> = arrayOfNulls(size)
        }
    }

    data class MapChange(val map: String) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(map)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<MapChange> {
            override fun createFromParcel(parcel: Parcel): MapChange = MapChange(parcel)

            override fun newArray(size: Int): Array<MapChange?> = arrayOfNulls(size)
        }
    }
}
