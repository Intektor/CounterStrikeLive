package de.intektor.counterstrikelive

/**
 * @author Intektor
 */

/**
 * Calls a response of the running matches that are captured by the game roundLog service. The response is delivered via ACTION_GET_RUNNING_MATCHES_RESPONSE.
 */
const val ACTION_GET_RUNNING_MATCHES = "action_counter_strike_live_get_running_matches"
/** String defining the sender*/
const val KEY_SENDER_KEY = "key_sender_key"

const val ACTION_GET_RUNNING_MATCHES_RESPONSE = "action_counter_strike_live_get_running_matches_response"
/** Long Array with the match ids*/
const val KEY_MATCH_IDS = "key_match_ids"

//Keys related to HLTVGameLogService#onStartCommand
/** Long with the match id */
const val KEY_MATCH_ID = "key_match_id"

const val ACTION_HLTV_MATCH_EVENT = "action_counter_strike_live_hltv_match_event"
const val KEY_HLTV_MATCH_EVENT = "key_hltv_match_event"