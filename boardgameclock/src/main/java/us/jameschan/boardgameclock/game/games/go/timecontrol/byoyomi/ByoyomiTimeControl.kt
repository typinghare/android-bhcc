package us.jameschan.boardgameclock.game.games.go.timecontrol.byoyomi

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.TimeControl
import us.jameschan.boardgameclock.game.TimerController
import us.jameschan.boardgameclock.game.settings.Setting

class ByoyomiTimeControl(override val game: Game) : TimeControl(game, "Byoyomi", DESCRIPTION) {
    companion object {
        const val SETTING_LABEL_TIME_PER_PERIOD = "Time/Period"
        const val SETTING_LABEL_PERIODS = "Periods"

        const val DESCRIPTION = """
            The byoyomi time control is.
        """
    }

    override fun initialize() {
        super.initialize()

        addSetting(
            Setting(
                SETTING_LABEL_TIME_PER_PERIOD,
                listOf("20 sec", "30 sec"),
                "30 sec"
            )
        )
        addSetting(Setting(SETTING_LABEL_PERIODS, listOf("1", "3", "5"), "3"))
    }

    override fun getTimerController(): TimerController {
        val main = getValueOfSettingsByLabel(SETTINGS_LABEL_MAIN, "1 min")
        val timePerPeriod = getValueOfSettingsByLabel(SETTING_LABEL_TIME_PER_PERIOD, "30 sec")
        val periods = getValueOfSettingsByLabel(SETTING_LABEL_PERIODS, "1")

        return ByoyomiTimerController(
            game,
            HourMinuteSecond.parse(main),
            HourMinuteSecond.parse(timePerPeriod),
            periods.toInt()
        )
    }
}