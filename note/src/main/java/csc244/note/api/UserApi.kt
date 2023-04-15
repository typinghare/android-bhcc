package csc244.note.api

import com.android.volley.AuthFailureError
import com.android.volley.Response.ErrorListener
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import csc244.note.common.web.Listener
import csc244.note.common.web.Request
import csc244.note.dto.ForgetPasswordDto
import csc244.note.dto.ForgetPasswordResponseDto
import csc244.note.dto.RefreshTokenResponseDto
import csc244.note.dto.RegisterAccountDto
import csc244.note.dto.UserDto
import csc244.note.dto.UserSignInDto
import csc244.note.dto.UserSignInResponseDto
import org.json.JSONObject


object UserApi {
    private const val API_METHOD_FORGET_PASSWORD = "forgotPassword"
    private const val API_METHOD_SIGN_IN = "authenticate"
    private const val API_METHOD_SIGN_OUT = "signOut"
    private const val API_METHOD_REFRESH_TOKEN = "refresh"
    private const val API_METHOD_GET_ALL_ACCOUNT = "getAllAccount"
    private const val API_METHOD_CREATE_ACCOUNT = "createAccount"
    private const val API_METHOD_DELETE_ACCOUNT = "deleteAccount"
    private const val API_METHOD_UPDATE_ACCOUNT = "setAccount"
    private const val API_METHOD_REGISTER_ACCOUNT = "registerAccount"

    /**
     * User forgets password. This API allows user to sign up.
     */
    fun forgetPassword(
        forgetPasswordDto: ForgetPasswordDto,
        callback: (forgetPasswordResponseDto: ForgetPasswordResponseDto) -> Unit,
        errorListener: ErrorListener
    ): Request<ForgetPasswordResponseDto> {
        val map = mutableMapOf<String, Any>()
        map["email"] = forgetPasswordDto.email!!

        return Api.post(
            API_METHOD_FORGET_PASSWORD,
            map,
            Listener(ForgetPasswordResponseDto::class, callback),
            errorListener
        )
    }

    /**
     * User signs in.
     */
    fun signIn(
        userLoginDto: UserSignInDto,
        callback: (userSignInResponseDto: UserSignInResponseDto) -> Unit,
        errorListener: ErrorListener
    ): Request<UserSignInResponseDto> {
        val map = mutableMapOf<String, Any>()
        map["email"] = userLoginDto.email!!
        map["password"] = userLoginDto.password!!

        return Api.post(
            API_METHOD_SIGN_IN,
            map,
            Listener(UserSignInResponseDto::class, callback),
            errorListener
        )
    }

    /**
     * User signs out.
     */
    fun signOut(
        callback: () -> Unit,
        errorListener: ErrorListener
    ): Request<Any> {
        return Api.post(
            API_METHOD_SIGN_OUT,
            null,
            Listener(callback),
            errorListener
        )
    }

    /**
     * Refreshes token.
     */
    fun refreshToken(
        callback: (refreshTokenResponseDto: RefreshTokenResponseDto) -> Unit,
        errorListener: ErrorListener
    ): Request<RefreshTokenResponseDto> {
        return Api.post(
            API_METHOD_REFRESH_TOKEN,
            null,
            Listener(RefreshTokenResponseDto::class, callback),
            errorListener
        )
    }

    /**
     * Returns all accounts.
     */
    fun getAllAccounts(
        callback: (userList: List<UserDto>) -> Unit,
        errorListener: ErrorListener
    ): JsonArrayRequest {
        val jsonObject = JSONObject()
        jsonObject.put("method", API_METHOD_GET_ALL_ACCOUNT)

        return object : JsonArrayRequest(
            Method.GET, Api.URL, null, { response ->
                val userList = mutableListOf<UserDto>()
                for (i in 0 until response.length()) {
                    val account = response.getJSONObject(i)
                    val userDto = UserDto()

                    userDto.email = account.getString("email")
                    userDto.firstName = account.getString("first_name")
                    userDto.lastName = account.getString("last_name")
                    userDto.extra = account.getString("extra")

                    userList.add(userDto)
                }

                callback(userList)
            }, errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["autho_token"] = ""

                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray()
            }
        }
    }

    /**
     * Creates an account.
     */
    fun createAccount(
        userDto: UserDto,
        callback: (userDto: UserDto) -> Unit,
        errorListener: ErrorListener
    ): Request<UserDto> {
        val map = mutableMapOf<String, Any>()
        map["email"] = userDto.email!!
        map["first_name"] = userDto.firstName!!
        map["last_name"] = userDto.lastName!!
        map["extra"] = userDto.extra!!

        return Api.post(
            API_METHOD_CREATE_ACCOUNT,
            map,
            Listener(UserDto::class, callback),
            errorListener
        )
    }

    /**
     * Deletes this account.
     */
    fun deleteAccount(callback: () -> Unit, errorListener: ErrorListener): Request<Any> {
        return Api.post(
            API_METHOD_DELETE_ACCOUNT,
            null,
            Listener(callback),
            errorListener
        )
    }

    /**
     * Updates an account.
     */
    fun updateAccount(
        userDto: UserDto,
        callback: (userDto: UserDto) -> Unit,
        errorListener: ErrorListener
    ): Request<UserDto> {
        val map = mutableMapOf<String, Any>()
        val account = JSONObject()
        map["account"] = account

        account.put("first_name", userDto.firstName)
        account.put("last_name", userDto.lastName)
        account.put("extra", userDto.extra)

        return Api.post(
            API_METHOD_UPDATE_ACCOUNT,
            map,
            Listener(UserDto::class, callback),
            errorListener
        )
    }

    /**
     * Registers an account.
     */
    fun registerAccount(
        registerAccountDto: RegisterAccountDto,
        callback: () -> Unit,
        errorListener: ErrorListener
    ): Request<Any> {
        val map = mutableMapOf<String, Any>()
        map["email"] = registerAccountDto.email!!
        map["temp_password"] = registerAccountDto.tempPassword!!
        map["password"] = registerAccountDto.password!!

        return Api.post(
            API_METHOD_REGISTER_ACCOUNT,
            map,
            Listener(callback),
            errorListener
        )
    }
}