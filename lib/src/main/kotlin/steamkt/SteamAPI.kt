package steamkt

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import steamkt.Models.ISteamUserInterface.SteamFriend
import steamkt.Models.ISteamUserInterface.SteamUserProfile
import java.lang.Exception
import javax.annotation.Nullable
import kotlin.text.StringBuilder

class SteamAPI(
    private val steamLogin: String,
    private val steamPassword: String,
    private val steamWebAPIKey: String
) {
    private val okHttpClient: OkHttpClient = OkHttpClient()
    private val moshi: Moshi = Moshi.Builder().build()
    private val jsonAdapter = moshi.adapter(Map::class.java)
    private val logger = LoggerFactory.getLogger(SteamAPI::class.java)

    private val baseApi = "https://api.steampowered.com"

    fun getUserDetails(steamId: Long) : SteamUserProfile {
        val args = mapOf(
            "steamids" to steamId,
            "key" to steamWebAPIKey,
            "format" to "json"
        )
        val getPlayerSummariesResponse = sendApiRequest("ISteamUser", "GetPlayerSummaries", "2", args)?.get("response") as Map<String, *>
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

        // TODO: Make an SteamAPI exception handler
        // TODO: Rework this shit below :P

        val steamUserProfile =  SteamUserProfile()
        steamUserProfile.steamId = steamId
        steamUserProfile.userName = userData["personaname"] as String
        steamUserProfile.avatarUrl = userData["avatarfull"] as String
        steamUserProfile.createdAt = (userData["timecreated"] as Double).toLong()
        steamUserProfile.isCommunityBanned = userBanData["CommunityBanned"] as Boolean
        steamUserProfile.isVACBanned = userBanData["VACBanned"] as Boolean
        steamUserProfile.VACBanCount = userBanData["NumberOfVACBans"] as Double
        steamUserProfile.daysSinceLastBan = userBanData["DaysSinceLastBan"] as Double
        steamUserProfile.gameBanCount = userBanData["NumberOfGameBans"] as Double
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
        return try {
            okHttpClient.newCall(request).execute().body?.string()?.let { jsonAdapter.fromJson(it) }
        } catch (e: Exception) {
            logger.warn("A $e was thrown when sending the API Request ${steamInterface}/${method}/v${version}")
            null
        }
    }
}
