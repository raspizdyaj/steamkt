package steamkt.Models.ISteamUserInterface

class SteamUserProfile {
    var steamId: Long? = null
    var userName: String? = null
    var profileUrl: String = "null"
    var avatarUrl: String? = null
    var createdAt: Long? = null

    var friendList: List<SteamFriend> = listOf()
    var isCommunityBanned: Boolean = false
    var isVACBanned: Boolean = false
    var VACBanCount: Int = 0
    var daysSinceLastBan: Int = 0
    var gameBanCount: Int = 0
}