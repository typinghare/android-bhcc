package us.jameschan.boardgameclock

import us.jameschan.boardgameclock.dto.UserSettingsDto

object LocalUser {
    var userId: Long? = null
    var token: String? = null

    var language: String = "english"
    var clickingSoundEffect: Boolean = true
    var warningSoundEffect: Boolean = true

    fun settings(userSettingsDto: UserSettingsDto) {
        language = userSettingsDto.language.lowercase()
        clickingSoundEffect = userSettingsDto.clickingSoundEffect
        warningSoundEffect = userSettingsDto.warningSoundEffect

        Application.refreshSettings()
    }

    fun isSignedIn(): Boolean {
        return userId != null
    }
}