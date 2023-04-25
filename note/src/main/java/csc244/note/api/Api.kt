package csc244.note.api

import com.android.volley.Request.Method
import com.android.volley.Response.ErrorListener
import com.google.gson.Gson
import csc244.note.common.web.Listener
import csc244.note.common.web.Request
import org.json.JSONObject

/**
 * @link https://docs.google.com/document/d/1zCuMnHhSF6D5VzZpuLQwFJte7p7_KbO_ncpq3nNs-Oo/edit
 */
object Api {
    const val URL = "http://192.168.0.7:8000/"

    private val gson = Gson()

    fun <T : Any> post(
        method: String,
        requestObject: Map<String, *>?,
        listener: Listener<T>,
        errorListener: ErrorListener,
    ): Request<T> {
        val request = Request(Method.POST, URL, listener)

        val jsonObject = JSONObject()
        jsonObject.put("method", method)

        if (requestObject != null) {
            for ((key, value) in requestObject) jsonObject.put(key, value)
        }

        return request.setJsonObject(jsonObject).setErrorListener(errorListener)
    }
}