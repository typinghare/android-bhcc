package csc244.note.common

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.ErrorListener
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * A simple encapsulation for JSON object request.
 * @author James Chan
 */
class Request<T>(private val method: Int, private val url: String) {
    private var jsonObject: JSONObject? = null
    private var listener: Listener<T>? = null
    private var errorListener: ErrorListener = ErrorListener { error ->
        val message: String? = error.message
        Log.d("RequestError", "[$method]$url : $message")
    }

    fun setJsonObject(jsonObject: JSONObject): Request<T> {
        this.jsonObject = jsonObject

        return this
    }

    fun setListener(listener: Listener<T>): Request<T> {
        this.listener = listener

        return this
    }

    fun setErrorListener(errorListener: ErrorListener): Request<T> {
        this.errorListener = errorListener

        return this
    }

    fun connect(requestQueue: RequestQueue) {
        if (listener == null) {
            throw Exception("Listener cannot be null.")
        }

        val requestListener = Response.Listener<JSONObject> { response ->
            val dtoClass = listener!!.getDtoClass()
            val dto = dtoClass.getConstructor().newInstance()

            val primaryConstructor = dtoClass::class.primaryConstructor
            val parameterNames = primaryConstructor?.parameters?.map { it.name }
            if (parameterNames != null) {
                for (parameterName in parameterNames) {
                    val myProperty = dtoClass::class.memberProperties.find { it.name == parameterName }
                    myProperty.toString()
                }
            }
        }

        requestQueue.add(JsonObjectRequest(method, url, jsonObject, requestListener, errorListener))
    }
}