package csc244.note.common;

import org.json.JSONObject
import java.io.File

object JsonLocalStorage {
    fun store(jsonFile: File, jsonObject: JSONObject) {
        LocalStorage.write(jsonFile, jsonObject.toString())
    }

    fun retrieve(jsonFile: File): JSONObject {
        val jsonString: String = LocalStorage.read(jsonFile)

        return JSONObject(jsonString)
    }
}
