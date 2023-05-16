package us.jameschan.boardgameclock.game.games.go.timecontrol.yingshi

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.game.TimeControl
import us.jameschan.boardgameclock.game.TimerController
import us.jameschan.boardgameclock.settings.Setting
import us.jameschan.boardgameclock.settings.SettingManager

class YingshiTimeControl(
    override val game: Game,
    private val role: Role?
) : TimeControl(game, role, "Yingshi", DESCRIPTION) {
    companion object {
        const val SETTING_LABEL_PENALTY = "Penalty"
        const val SETTING_LABEL_MAX_PENALTY = "MaxPenalty"

        const val DESCRIPTION = """
            The byoyomi time control is.
        """
    }

    override fun initialize() {
        super.initialize()

        val penaltySetting = Setting(SETTING_LABEL_PENALTY, "30 min").apply {
            setExplanation("When the main time runs out, the penalty time is used.")
        }
        val maxPenaltySetting = Setting(SETTING_LABEL_MAX_PENALTY, "2").apply {
            setExplanation("The maximum number of penalty times. Clock stops will all penalty times are used.")
        }

        SettingManager.setSetting(
            this.javaClass.name + "." + SETTING_LABEL_PENALTY + "." + role.toString(),
            penaltySetting
        )
        SettingManager.setSetting(
            this.javaClass.name + "." + SETTING_LABEL_MAX_PENALTY + "." + role.toString(),
            maxPenaltySetting
        )

        addSetting(penaltySetting)
        addSetting(maxPenaltySetting)
    }

    override fun getTimerController(): TimerController {
        val main = getValueOfSettingsByLabel(SETTINGS_LABEL_MAIN, "60 min")
        val penalty = getValueOfSettingsByLabel(SETTING_LABEL_PENALTY, "30 min")
        val maxPenalty = getValueOfSettingsByLabel(SETTING_LABEL_MAX_PENALTY, "3")

        return YingshiTimerController(
            game,
            role!!,
            HourMinuteSecond.parse(main),
            HourMinuteSecond.parse(penalty),
            maxPenalty.toInt()
        )
    }
}
