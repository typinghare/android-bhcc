package csc244.note.common

import org.json.JSONObject
import java.io.File

object User {
    private var token: String? = null

    fun setToken(token: String) {
        this.token = token
    }

    fun saveToken(file: File) {
        JsonLocalStorage.store(file, JSONObject().apply { put("token", token) })
    }

    fun removeToken(file: File) {
        token = null

        if (file.exists()) file.delete()
    }

    fun retrieveToken(file: File) {
        val jsonObject = JsonLocalStorage.retrieve(file)
        if (jsonObject.has("token")) {
            this.token = jsonObject.getString("token")
        }
    }

    fun getToken(): String? {
        return token
    }
}