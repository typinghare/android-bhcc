package us.jameschan.boardgameclock

import com.android.volley.Response.ErrorListener
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import us.jameschan.boardgameclock.dto.UserDto
import us.jameschan.boardgameclock.dto.UserSettingsDto

object Api {
    // Domain.
    private const val DOMAIN = "http://192.168.0.7:8080"

    // URLs.
    private const val URL_SIGN_IN = "/users/"
    private const val URL_SIGN_UP = "/users/"
    private const val URL_GET_SETTINGS = "/users/{userId}/settings"
    private const val URL_UPDATE_SETTINGS = "/users/{userId}/settings"

    private fun url(url: String, queryString: String? = null): String {
        return "${DOMAIN}${url}${if (queryString != null) "?${queryString}" else ""}"
    }

    /**
     * User signs in.
     */
    fun signIn(
        username: String,
        password: String,
        callback: (UserDto) -> Unit,
        errorListener: ErrorListener
    ): JsonObjectRequest {
        val body = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        return object : JsonObjectRequest(Method.PUT, url(URL_SIGN_IN), body, { response ->
            val data = response.getJSONObject("data")

            val userDto = UserDto(
                data.getLong("userId"),
                data.getString("username"),
                data.getString("token")
            )

            callback(userDto)
        }, errorListener) {}
    }

    /**
     * User signs up.
     */
    fun signUp(
        username: String,
        password: String,
        callback: (UserDto) -> Unit,
        errorListener: ErrorListener
    ): JsonObjectRequest {
        val body = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        return object : JsonObjectRequest(Method.POST, url(URL_SIGN_UP), body, { response ->
            val data = response.getJSONObject("data")
            val userDto = UserDto(
                data.getLong("userId"),
                data.getString("username"),
                data.getString("token")
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
        val url = url(URL_GET_SETTINGS.replace("{userId}", userId.toString()))

        return object : JsonObjectRequest(Method.GET, url, null, { response ->
            val data = response.getJSONObject("data")
            val userSettingsDto = UserSettingsDto(
                data.getLong("userId"),
                data.getString("language"),
                data.getBoolean("clickingSoundEffect"),
                data.getBoolean("warningSoundEffect")
            )

            callback(userSettingsDto)
        }, errorListener) {}
    }

    /**
     * Update user settings.
     */
    fun updateUserSettings(
        userId: Long,
        userSettingsDto: UserSettingsDto,
        callback: (UserSettingsDto) -> Unit,
        errorListener: ErrorListener
    ): JsonObjectRequest {
        val url = url(URL_UPDATE_SETTINGS.replace("{userId}", userId.toString()))
        val jsonObject = JSONObject().apply {
            put("userId", userSettingsDto.userId.toString())
            put("language", userSettingsDto.language)
            put("clickingSoundEffect", userSettingsDto.clickingSoundEffect.toString())
            put("warningSoundEffect", userSettingsDto.warningSoundEffect.toString())
        }

        return object : JsonObjectRequest(Method.PUT, url, jsonObject, { response ->
            val data = response.getJSONObject("data")
            val newUserSettingsDto = UserSettingsDto(
                data.getLong("userId"),
                data.getString("language"),
                data.getBoolean("clickingSoundEffect"),
                data.getBoolean("warningSoundEffect")
            )

            callback(newUserSettingsDto)
        }, errorListener) {}
    }
}