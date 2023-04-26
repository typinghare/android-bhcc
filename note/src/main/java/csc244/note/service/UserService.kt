package csc244.note.service

import android.content.Context
import com.android.volley.Response.ErrorListener
import csc244.note.api.UserApi
import csc244.note.common.User
import csc244.note.common.web.Request
import csc244.note.dto.ForgetPasswordDto
import csc244.note.dto.ForgetPasswordResponseDto
import csc244.note.dto.RegisterAccountDto
import csc244.note.dto.UserDto
import csc244.note.dto.UserSignInDto
import csc244.note.dto.UserSignInResponseDto
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
        errorListener: ErrorListener,
        successCallback: () -> Unit,
    ): Request<UserSignInResponseDto> {
        val userSignInDto = UserSignInDto().apply {
            this.email = email
            this.password = password
        }

        return UserApi.signIn(userSignInDto, { userSignInResponseDto ->
            val token: String = userSignInResponseDto.token.toString()
            val tokenFile = File(context.filesDir, TOKEN_FILEPATH)
            User.setToken(token)
            User.saveToken(tokenFile)

            successCallback()
        }, errorListener)
    }

    fun newUser(
        userDto: UserDto,
        errorListener: ErrorListener,
        successCallback: () -> Unit
    ): Request<UserDto> {
        return UserApi.createAccount(userDto, {
            successCallback()
        }, errorListener)
    }

    fun registerAccount(
        userRegisterAccountDto: RegisterAccountDto,
        errorListener: ErrorListener,
        successCallback: () -> Unit
    ): Request<Any> {
        return UserApi.registerAccount(userRegisterAccountDto, successCallback, errorListener)
    }

    fun resetPassword(
        email: String,
        errorListener: ErrorListener,
        successCallback: () -> Unit
    ): Request<ForgetPasswordResponseDto> {
        return UserApi.forgetPassword(ForgetPasswordDto().apply { this.email = email }, {
            successCallback()
        }, errorListener)
    }

   fun signOut(
       errorListener: ErrorListener,
       successCallback: () -> Unit
   ):Request<Any> {
       return UserApi.signOut(successCallback, errorListener)
   }
}