package us.jameschan.boardgameclock.game

import us.jameschan.boardgameclock.game.settings.Setting
import us.jameschan.boardgameclock.game.settings.Settings

open class TimeControl(
    private val name: String,
    private val description: String,
) : Settings(), Initializer {
    companion object {
        const val SETTINGS_LABEL_MAIN = "Main"
    }

    override fun initialize() {
        addSetting(Setting(SETTINGS_LABEL_MAIN, listOf("10 min", "30 min"), "10 min"))
    }

    fun getName(): String {
        return name
    }

    fun getDescription(): String {
        return description
    }

    open fun getTimerController(): TimerController {
        val main: String = getValueOfSettingsByLabel(SETTINGS_LABEL_MAIN, "1 min")
        val mainTime = HourMinuteSecond.parse(main)

        return TimerController(mainTime)
    }
}