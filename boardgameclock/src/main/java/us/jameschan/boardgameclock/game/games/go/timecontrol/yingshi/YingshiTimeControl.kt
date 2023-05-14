package us.jameschan.boardgameclock.game.games.go.timecontrol.yingshi

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.TimeControl
import us.jameschan.boardgameclock.game.TimerController
import us.jameschan.boardgameclock.game.settings.Setting

class YingshiTimeControl(
    override val game: Game
) : TimeControl(game, "Yingshi", DESCRIPTION) {
    companion object {
        const val SETTING_LABEL_PENALTY = "Penalty"
        const val SETTING_LABEL_MAX_PENALTY = "MaxPenalty"

        const val DESCRIPTION = """
            The byoyomi time control is.
        """
    }

    override fun initialize() {
        super.initialize()

        addSetting(Setting(SETTING_LABEL_PENALTY, "30 min"))
        addSetting(Setting(SETTING_LABEL_MAX_PENALTY, "2"))
    }

    override fun getTimerController(): TimerController {
        val main = getValueOfSettingsByLabel(SETTINGS_LABEL_MAIN, "60 min")
        val penalty = getValueOfSettingsByLabel(SETTING_LABEL_PENALTY, "30 min")
        val maxPenalty = getValueOfSettingsByLabel(SETTING_LABEL_MAX_PENALTY, "3")

        return YingshiTimerController(
            game,
            HourMinuteSecond.parse(main),
            HourMinuteSecond.parse(penalty),
            maxPenalty.toInt()
        )
    }
}
