/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package steamkt

import kotlin.test.Test
import kotlin.test.assertTrue

class LibraryTest {
    @Test fun someLibraryMethodReturnsTrue() {
        val steamAPI = SteamAPI("", "", "")
        println(steamAPI.getUserDetails(0L)?.userName)
    }
}