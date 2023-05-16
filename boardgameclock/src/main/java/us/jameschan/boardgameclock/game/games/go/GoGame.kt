package us.jameschan.boardgameclock.game.games.go

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.games.go.timecontrol.byoyomi.ByoyomiTimeControl
import us.jameschan.boardgameclock.game.games.go.timecontrol.yingshi.YingshiTimeControl
import us.jameschan.boardgameclock.settings.Setting
import us.jameschan.boardgameclock.settings.SettingManager

class GoGame : Game() {
    companion object {
        const val SETTING_ZERO_AS_LAST_PERIOD = "Zero as Last Period."
    }

    override fun initialize() {
        addTimeControl(ByoyomiTimeControl(this, null))
        addTimeControl(YingshiTimeControl(this, null))

        // Sets an advanced setting.
        val jSetting =
            Setting(SETTING_ZERO_AS_LAST_PERIOD, "true", Setting.Companion.Type.BOOL).apply {
                setExplanation(
                    "When this is selected, the last" +
                            " period is counted when the number on the left top is zero; otherwise, " +
                            "the last period is on when the number on the left top is one."
                )
            }

        addSetting(jSetting)
        SettingManager.setSetting(this.javaClass.name + "." + SETTING_ZERO_AS_LAST_PERIOD, jSetting)
    }
}
