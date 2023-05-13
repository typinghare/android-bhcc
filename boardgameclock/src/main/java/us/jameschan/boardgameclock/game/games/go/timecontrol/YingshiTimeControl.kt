package us.jameschan.boardgameclock.game.games.go.timecontrol

import us.jameschan.boardgameclock.game.TimeControl
import us.jameschan.boardgameclock.game.settings.Setting

class YingshiTimeControl: TimeControl("Yingshi", DESCRIPTION) {
    companion object {
        const val DESCRIPTION = """
            The byoyomi time control is.
        """
    }

    override fun initialize() {
        addSetting(Setting("Main", listOf("10 min", "30 min"), "10 min"))
        addSetting(Setting("Penalty", listOf("20 min", "30 min"), "30 min"))
        addSetting(Setting("MaxPenaltyTimes", listOf("20 min", "30 min"), "30 min"))
    }
}
