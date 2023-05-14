package us.jameschan.boardgameclock.settings;

object SettingManager {
    private val settingMap: MutableMap<String, Setting> = mutableMapOf()

    fun getSetting(name: String): Setting? {
        return settingMap[name]
    }

    fun setSetting(name: String, setting: Setting) {
        settingMap[name] = setting
    }

    fun getSettingName(setting: Setting): String? {
        for ((name, s) in settingMap) {
            if (s == setting) return name
        }
        return null
    }
}
