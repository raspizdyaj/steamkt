package steamkt.Models.ISteamUserInterface

class SteamUserProfile {
    var steamId: Long? = null
    var userName: String? = null
    var profileUrl: String? = null
    var avatarUrl: String? = null
    var createdAt: Long? = null

    var friendList: List<SteamFriend>? = null
    // forEach || ISteamUser/GetFriendList/v1
    var isCommunityBanned: Boolean? = null
    var isVACBanned: Boolean? = null
    var VACBanCount: Double? = null
    var daysSinceLastBan: Double? = null
    var gameBanCount: Double? = null
}