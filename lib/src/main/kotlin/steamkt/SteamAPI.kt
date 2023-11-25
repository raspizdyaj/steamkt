package steamkt

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import steamkt.Models.ISteamUserInterface.SteamFriend
import steamkt.Models.ISteamUserInterface.SteamUserProfile
import steamkt.exceptions.SteamWebAPIException
import java.lang.Exception
import javax.annotation.Nullable
import kotlin.math.roundToInt
import kotlin.text.StringBuilder

@Suppress("unchecked_cast") // TODO: Rework this shit
class SteamAPI(
//    private val steamLogin: String,
//    private val steamPassword: String,
    private val steamWebAPIKey: String
) {
    private val okHttpClient: OkHttpClient = OkHttpClient()
    private val moshi: Moshi = Moshi.Builder().build()
    private val jsonAdapter = moshi.adapter(Map::class.java)

    private val baseApi = "https://api.steampowered.com"
    // make a rate-limiter that store rate limit (3600 per hour) and current count of requests
    // maybe class or internal function

    fun getUserDetails(steamId: Long) : SteamUserProfile {
        val args = mapOf(
            "steamids" to steamId,
            "key" to steamWebAPIKey,
            "format" to "json"
        )
        val getPlayerSummariesResponse = sendApiRequest("ISteamUser", "GetPlayerSummaries", "2", args)?.get("response") as Map<String, *>
        if ((getPlayerSummariesResponse["players"] as List<*>).isEmpty()) {
            throw SteamWebAPIException("The user with the specified Steam ID was not found.")
        }
        val userData: Map<String, *> = (getPlayerSummariesResponse["players"] as List<*>)[0] as Map<String,*>

        val getPlayerBansResponse = sendApiRequest("ISteamUser", "GetPlayerBans", "1", args)?.get("players") as List<*>
        val userBanData = getPlayerBansResponse[0] as Map<String, *>

        val argsGetFriendsList = mapOf(
            "steamid" to steamId,
            "key" to steamWebAPIKey,
            "format" to "json"
        )

        val getFriendListResponse = sendApiRequest("ISteamUser", "GetFriendList", "1", argsGetFriendsList)?.get("friendslist") as Map<String, *>
        val friendListRaw = getFriendListResponse["friends"] as List<Map<String, *>>
        val friendList = mutableListOf<SteamFriend>()

        friendListRaw.forEach {
            friendList.add(SteamFriend(it))
        }

        // TODO: Rework this shit below :P

        val steamUserProfile =  SteamUserProfile()
        steamUserProfile.steamId = steamId
        steamUserProfile.userName = userData["personaname"] as String
        steamUserProfile.avatarUrl = userData["avatarfull"] as String
        steamUserProfile.createdAt = (userData["timecreated"] as Double).toLong()
        steamUserProfile.isCommunityBanned = userBanData["CommunityBanned"] as Boolean
        steamUserProfile.isVACBanned = userBanData["VACBanned"] as Boolean
        steamUserProfile.VACBanCount = (userBanData["NumberOfVACBans"] as Double).roundToInt()
        steamUserProfile.daysSinceLastBan = (userBanData["DaysSinceLastBan"] as Double).roundToInt()
        steamUserProfile.gameBanCount = (userBanData["NumberOfGameBans"] as Double).roundToInt()
        steamUserProfile.friendList = friendList
        steamUserProfile.profileUrl = "https://steamcommunity.com/profiles/${steamId}"

        return steamUserProfile
    }

    private fun sendApiRequest(steamInterface: String, method: String, version: String, @Nullable args: Map<String, *>?) : Map<*, *>? {
        val query: StringBuilder
        if(args != null) {
            query = StringBuilder("?")
            args.entries.forEach { query.append(it.key).append("=").append(it.value).append("&") }
        } else {
            query = StringBuilder()
        }
        val request = Request.Builder()
            .url("${baseApi}/${steamInterface}/${method}/v${version}/${query.substring(0, query.length-1)}")
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            val errorMsg: String
            val regexPattern = Regex("</h1>(.*?)</body>", RegexOption.DOT_MATCHES_ALL)
            errorMsg = when(response.code) {
                200 -> return response.body?.string()?.let { jsonAdapter.fromJson(it) }
                400 -> {
                    "400 Bad request | ${response.body?.string()?.let {
                        regexPattern.find(it)!!.groupValues[1]
                    }}"
                }

                403 -> {
                    "403 Forbidden | Check your API key"
                }

                else -> {
                    "${response.code} Error | Unknown error"
                }
            }
            throw SteamWebAPIException("Request failed when sending API request ${steamInterface}/${method}/v${version}\n${errorMsg}")
        } catch (e: Exception) {
            throw SteamWebAPIException("A $e was thrown when sending the API Request ${steamInterface}/${method}/v${version}")
        }
    }
}
