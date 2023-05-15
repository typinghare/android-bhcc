package us.jameschan.boardgameclock.util

import com.google.gson.Gson
import us.jameschan.boardgameclock.settings.Setting
import us.jameschan.boardgameclock.settings.SettingManager

object JsonHelper {
    private val gson: Gson = Gson()

    fun toJson(settingList: List<Setting>): String {
        val nameList: MutableList<String> = mutableListOf()
        settingList.forEach {
            val name = SettingManager.getSettingName(it)
            if (name != null) nameList.add(name)
        }

        return gson.toJson(nameList)
    }

    fun fromJsonToSettingList(jsonString: String): List<Setting> {
        val settingList: MutableList<Setting> = mutableListOf()
        val stringList: Array<String> = gson.fromJson(jsonString, Array<String>::class.java)
        stringList.forEach {
            val setting = SettingManager.getSetting(it)
            if (setting != null) settingList.add(setting)
        }

        return settingList
    }
}