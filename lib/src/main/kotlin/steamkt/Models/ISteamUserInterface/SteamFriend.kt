package steamkt.Models.ISteamUserInterface

class SteamFriend(data: Map<String, *>) {
    var steamId: Long = (data["steamid"] as String).toLong()
    var relationShip: RelationshipType = RelationshipType.FRIEND // a temporary solution :b
    var friendSince: Double = data["friend_since"] as Double
}