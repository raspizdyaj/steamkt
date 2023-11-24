/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package steamkt

import kotlin.test.Test
import kotlin.test.assertTrue

class LibraryTest {
    @Test fun someLibraryMethodReturnsTrue() {
        val steamAPI = SteamAPI("", "", "FB754BC271C7DB3BA7E6717724176A7E")
        val steamUser = steamAPI.getUserDetails(76561198177219325)

        println("==========")
        println("Username: ${steamUser.userName}")
        println("URL: ${steamUser.profileUrl}")
        println("Friend count: ${steamUser.friendList?.count()}")
        println("Ban Count: ${steamUser.VACBanCount?.let { steamUser.gameBanCount?.plus(it) }}")
        println("==========")
    }
}
