package us.jameschan.boardgameclock.dto

data class UserDto(
    val userId: Long,
    val username: String,
    val token: String,
)