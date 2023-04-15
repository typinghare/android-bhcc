package csc244.note.common.web

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.ErrorListener
import com.android.volley.toolbox.JsonObjectRequest
import csc244.note.common.util.DataTransferObjects
import org.json.JSONObject

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
    private var errorListener: ErrorListener = ErrorListener { error ->
        val message: String? = error.message
        Log.d("RequestError", "[$method]$url : $message")
    }

    fun setJsonObject(jsonObject: JSONObject): Request<T> {
        this.jsonObject = jsonObject

        return this
    }

    fun setErrorListener(errorListener: ErrorListener): Request<T> {
        this.errorListener = errorListener

        return this
    }

    fun connect(requestQueue: RequestQueue) {
        val requestListener = Response.Listener<JSONObject> { response ->
            val map = mutableMapOf<String, Any?>()
            for (key in response.keys()) {
                map[key] = response[key]
            }

            val dto = DataTransferObjects.createDto(listener.getDtoClass(), map)
            listener.accept(dto)
        }

        requestQueue.add(JsonObjectRequest(method, url, jsonObject, requestListener, errorListener))
    }
}