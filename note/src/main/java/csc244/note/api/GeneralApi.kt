package csc244.note.api

import csc244.note.common.web.Listener
import csc244.note.common.web.Request
import com.android.volley.Request.Method
import com.google.gson.Gson
import org.json.JSONObject

object GeneralApi {
    private const val URL = "http://localhost:8000/"
    private val gson = Gson()

    fun <T : Any> post(
        method: String,
        requestObject: Map<String, *>?,
        listener: Listener<T>
    ): Request<T> {
        val request = Request(Method.POST, URL, listener)
        val jsonObject = mutableMapOf<String, Any>()
        jsonObject["method"] = method

        if (requestObject != null) {
            for ((key, value) in jsonObject) jsonObject[key] = value
        }

        return request
    }
}