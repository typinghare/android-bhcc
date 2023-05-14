package us.jameschan.boardgameclock;

import com.android.volley.Response.ErrorListener
import com.android.volley.toolbox.JsonObjectRequest
import us.jameschan.boardgameclock.dto.UserDto
import us.jameschan.boardgameclock.dto.UserSettingsDto

object Api {
    private const val URL_SIGN_IN = "/users/"
    private const val URL_SIGN_UP = "/users/"
    private const val URL_GET_SETTINGS = "/users/{userId}/settings"

    /**
     * User signs in.
     */
    fun signIn(
        callback: (UserDto) -> Unit,
        errorListener: ErrorListener
    ): JsonObjectRequest {
        return object : JsonObjectRequest(Method.PUT, URL_SIGN_IN, null, { response ->
            val userDto = UserDto(
                response.getLong("userId"),
                response.getString("username"),
                response.getString("token")
            )

            callback(userDto)
        }, errorListener) {}
    }

    /**
     * User signs up.
     */
    fun signUp(
        callback: (UserDto) -> Unit,
        errorListener: ErrorListener
    ): JsonObjectRequest {
        return object : JsonObjectRequest(Method.POST, URL_SIGN_UP, null, { response ->
            val userDto = UserDto(
                response.getLong("userId"),
                response.getString("username"),
                response.getString("token")
            )

            callback(userDto)
        }, errorListener) {}
    }

    /**
     * Gets user settings.
     */
    fun getUserSettings(
        userId: Long,
        callback: (UserSettingsDto) -> Unit,
        errorListener: ErrorListener
    ): JsonObjectRequest {
        val url = URL_GET_SETTINGS.replace("{userId}", userId.toString())

        return object : JsonObjectRequest(Method.GET, url, null, { response ->
            val userSettingsDto = UserSettingsDto(
                response.getLong("userId"),
                response.getString("language"),
                response.getBoolean("clickingSoundEffect"),
                response.getBoolean("warningSoundEffect")
            )

            callback(userSettingsDto)
        }, errorListener) {}
    }
}
