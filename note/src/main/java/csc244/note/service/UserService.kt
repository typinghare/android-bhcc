package csc244.note.service

import android.content.Context
import com.android.volley.Response
import csc244.note.api.UserApi
import csc244.note.common.User
import csc244.note.dto.UserSignInDto
import java.io.File

class UserService(private val context: Context) {
    companion object {
        const val TOKEN_FILEPATH = "token.json"
    }

    fun retrieveTokenFromFile() {
        User.retrieveToken(File(context.filesDir, TOKEN_FILEPATH))
    }

    fun signIn(
        email: String,
        password: String,
        errorListener: Response.ErrorListener,
        successCallback: () -> Unit,
    ) {
        val userSignInDto = UserSignInDto().apply {
            this.email = email
            this.password = password
        }

        UserApi.signIn(userSignInDto, { userSignInResponseDto ->
            val token: String = userSignInResponseDto.token.toString()

            val tokenFile = File(context.filesDir, TOKEN_FILEPATH)
            User.setToken(token)
            User.saveToken(tokenFile)

            successCallback()
        }, errorListener)
    }
}