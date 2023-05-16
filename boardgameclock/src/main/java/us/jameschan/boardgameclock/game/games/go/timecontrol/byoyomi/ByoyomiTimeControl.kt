package us.jameschan.boardgameclock.game.games.go.timecontrol.byoyomi

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.game.TimeControl
import us.jameschan.boardgameclock.game.TimerController
import us.jameschan.boardgameclock.settings.Setting
import us.jameschan.boardgameclock.settings.SettingManager

class ByoyomiTimeControl(
    override val game: Game,
    private val role: Role?
) : TimeControl(game, role, "Byoyomi", DESCRIPTION) {
    companion object {
        const val SETTING_LABEL_TIME_PER_PERIOD = "Time/Period"
        const val SETTING_LABEL_PERIODS = "Periods"

        const val DESCRIPTION = """
            The byoyomi time control is.
        """
    }

    override fun initialize() {
        val mainSetting = Setting(SETTINGS_LABEL_MAIN, "10 min").apply {
            setExplanation("The main time. When the main time run outs, the periods are utilized.")
        }
        val timePerPeriodSetting = Setting(SETTING_LABEL_TIME_PER_PERIOD, "5 sec").apply {
            setExplanation(
                "The time limit for each period. " +
                        "When the time for a period expires, " +
                        "the number of remaining periods is reduced by one."
            )
        }
        val periodsSetting = Setting(SETTING_LABEL_PERIODS, "3").apply {
            setExplanation("The number of periods. When all periods are consumed, the clock will stop.")
        }

        addSetting(mainSetting)
        addSetting(timePerPeriodSetting)
        addSetting(periodsSetting)

        SettingManager.setSetting(
            this.javaClass.name + "." + SETTINGS_LABEL_MAIN +
                    "." + role.toString(), mainSetting
        )
        SettingManager.setSetting(
            this.javaClass.name + "." + SETTING_LABEL_TIME_PER_PERIOD + "." + role.toString(),
            timePerPeriodSetting
        )
        SettingManager.setSetting(
            this.javaClass.name + "." + SETTING_LABEL_PERIODS + "." + role.toString(),
            periodsSetting
        )

    }

    override fun getTimerController(): TimerController {
        val main = getValueOfSettingsByLabel(SETTINGS_LABEL_MAIN, "1 min")
        val timePerPeriod = getValueOfSettingsByLabel(SETTING_LABEL_TIME_PER_PERIOD, "30 sec")
        val periods = getValueOfSettingsByLabel(SETTING_LABEL_PERIODS, "1")

        return ByoyomiTimerController(
            game,
            role!!,
            HourMinuteSecond.parse(main),
            HourMinuteSecond.parse(timePerPeriod),
            periods.toInt()
        )
    }
}