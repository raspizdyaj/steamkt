package steamkt.Models.ISteamUserInterface

class SteamUserProfile {
    var steamId: Long? = null
    var userName: String? = null
    var profileUrl: String? = null
    var avatarUrl: String? = null
    var primaryClanId: Long? = null
    var createdAt: Long? = null

    val friendList: List<SteamFriend> = listOf()
    // forEach || ISteamUser/GetFriendList/v1
    var isCommunityBanned: Boolean? = null
    var isVACBanned: Boolean? = null
    var VACBanCount: Int? = null
    var daysSinceLastBan: Int? = null
    var gameBanCount: Int? = null
    var economyBan: String? = null
}