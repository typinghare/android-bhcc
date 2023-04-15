package csc244.note.api

import csc244.note.common.web.Listener
import csc244.note.common.web.Request
import com.android.volley.Request.Method
import com.android.volley.Response.ErrorListener
import com.google.gson.Gson
import org.json.JSONObject

/**
 * @link https://docs.google.com/document/d/1zCuMnHhSF6D5VzZpuLQwFJte7p7_KbO_ncpq3nNs-Oo/edit
 */
object Api {
    const val URL = "http://localhost:8000/"

    private val gson = Gson()

    fun <T : Any> post(
        method: String,
        requestObject: Map<String, *>?,
        listener: Listener<T>,
        errorListener: ErrorListener,
        header: Map<String, String>? = null
    ): Request<T> {
        val request = Request(Method.POST, URL, listener)

        val jsonObject = JSONObject()
        jsonObject.put("method", method)
        jsonObject.put("time_unit", "SECONDS")
        jsonObject.put("time_span", 1)

        if (requestObject != null) {
            for ((key, value) in requestObject) jsonObject.put(key, value)
        }

        return request.setJsonObject(jsonObject).setErrorListener(errorListener)
    }
}