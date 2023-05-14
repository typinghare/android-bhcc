package us.jameschan.boardgameclock

import us.jameschan.boardgameclock.dto.UserSettingsDto

object LocalUser {
    var userId: Long? = null
    var token: String? = null
//    var language: String = "english"
    var language: String = "chinese"

    fun settings(userSettingsDto: UserSettingsDto) {
        language = userSettingsDto.language
    }
}