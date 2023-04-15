package csc244.note.api

import com.android.volley.Request.Method
import csc244.note.common.web.Listener
import csc244.note.common.web.Request
import csc244.note.dto.UserDto
import csc244.note.dto.UserLoginDto
import csc244.note.dto.UserLoginResponseDto

/**
 * @link https://docs.google.com/document/d/1zCuMnHhSF6D5VzZpuLQwFJte7p7_KbO_ncpq3nNs-Oo/edit
 */
object UserApi {
    private const val LOGIN_URL = ""

    /**
     * User logins.
     */
    fun login(
        email: String,
        password: String,
        callback: (userLoginResponseDto: UserLoginResponseDto) -> Unit
    ): Request<UserLoginResponseDto> {
        val userLoginDto = UserLoginDto()

        userLoginDto.email = email
        userLoginDto.password = password

        return Request(
            Method.POST,
            LOGIN_URL,
            Listener(UserLoginResponseDto::class, callback)
        )
    }
}