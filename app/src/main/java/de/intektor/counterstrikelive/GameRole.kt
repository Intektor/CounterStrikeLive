package de.intektor.counterstrikelive

/**
 * @author Intektor
 */
enum class GameRole {
    TERRORIST,
    COUNTER_TERRORIST,
    NOT_DEFINED;

    companion object {
        fun forHLTVName(name: String): GameRole {
            return when (name) {
                "CT" -> COUNTER_TERRORIST
                "TERRORIST" -> TERRORIST
                else -> NOT_DEFINED
            }
        }
    }

}