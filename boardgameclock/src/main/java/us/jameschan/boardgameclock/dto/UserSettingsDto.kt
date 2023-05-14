package us.jameschan.boardgameclock.dto

data class UserSettingsDto(
    val userId: Long,
    val language: String,
    val clickingSoundEffect: Boolean,
    val warningSoundEffect: Boolean
)