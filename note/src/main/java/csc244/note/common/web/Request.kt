package csc244.note.common.web

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.ErrorListener
import com.android.volley.toolbox.JsonObjectRequest
import csc244.note.common.User
import csc244.note.common.util.DataTransferObjects
import org.json.JSONObject
import kotlin.reflect.KClass

/**
 * A simple encapsulation for JSON object request.
 * @author James Chan
 */
class Request<T : Any>(
    private val method: Int,
    private val url: String,
    private val listener: Listener<T>
) {
    private var jsonObject: JSONObject? = null
    private var header: Map<String, String>? = null
    private var errorListener: ErrorListener = ErrorListener { error ->
        val message: String? = error.message
        Log.d("RequestError", "[$method]$url : $message")
    }

    fun setJsonObject(jsonObject: JSONObject?): Request<T> {
        if (jsonObject != null) this.jsonObject = jsonObject

        return this
    }

    fun setHeader(header: Map<String, String>?): Request<T> {
        if (header != null) this.header = header

        return this
    }

    fun setErrorListener(errorListener: ErrorListener?): Request<T> {
        if (errorListener != null) this.errorListener = errorListener

        return this
    }

    fun connect(requestQueue: RequestQueue) {
        val requestListener = Response.Listener<JSONObject> { response ->
            val map = mutableMapOf<String, Any?>()
            for (key in response.keys()) {
                // key mapping: from snake_case to camelCase
                val realKey: String = snakeCaseToCamelCase(key)
                map[realKey] = response[key]
            }

            val dtoClass: KClass<T>? = listener.getDtoClass()
            if (dtoClass != null) {
                listener.accept(DataTransferObjects.createDto(dtoClass, map))
            } else {
                listener.accept()
            }
        }

        val jsonObjectRequest =
            object : JsonObjectRequest(method, url, jsonObject, requestListener, errorListener) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val token = User.getToken().toString()
                    Log.d("UsingToken", token)

                    return HashMap<String, String>().apply {
                        put("autho_token", token)
                    }
                }
            }

        requestQueue.add(jsonObjectRequest)
    }

    private fun snakeCaseToCamelCase(input: String): String {
        return buildString {
            var capitalizeNext = false
            for (char in input) {
                capitalizeNext = if (char == '_') {
                    true
                } else {
                    append(if (capitalizeNext) char.uppercaseChar() else char.lowercaseChar())
                    false
                }
            }
        }
    }
}