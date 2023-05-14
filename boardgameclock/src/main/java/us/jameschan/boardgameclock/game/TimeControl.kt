package us.jameschan.boardgameclock.game

import us.jameschan.boardgameclock.game.settings.Setting
import us.jameschan.boardgameclock.game.settings.Settings

/**
 * A time control contains several settings, which can be registered by `addSetting` method. More
 * settings can be added by overriding `initialize` method in children class.
 * One time control corresponds to one timer controller.
 * @see TimerController
 */
open class TimeControl(
    protected open val game: Game,
    private val name: String,
    private val description: String,
) : Settings(), Initializer {
    companion object {
        const val SETTINGS_LABEL_MAIN = "Main"
    }

    override fun initialize() {
        addSetting(Setting(SETTINGS_LABEL_MAIN, "10 min"))
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

        return TimerController(game, mainTime)
    }
}