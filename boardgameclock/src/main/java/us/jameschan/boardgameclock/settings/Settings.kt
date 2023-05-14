package us.jameschan.boardgameclock.settings

/**
 * A settings container.
 */
abstract class Settings {
    private val settingList: MutableList<Setting> = mutableListOf()

    protected fun addSetting(setting: Setting) {
        settingList.add(setting)
    }

    fun getSettingList(): List<Setting> {
        return settingList
    }

    private fun getSettingByLabel(label: String): Setting? =
        getSettingList().find { it.getLabel() == label }

    fun getSettingValueByLabel(label: String) = getSettingByLabel(label)?.getValue()

    fun getValueOfSettingsByLabel(label: String, defaultValue: String) =
        getSettingByLabel(label)?.getValue() ?: defaultValue

    fun clearAllSettings() {
        settingList.clear()
    }
}