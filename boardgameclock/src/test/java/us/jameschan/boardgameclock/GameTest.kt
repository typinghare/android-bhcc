package us.jameschan.boardgameclock

import org.junit.Test
import us.jameschan.boardgameclock.game.games.go.GoGame

class GameTest {
    @Test
    fun buildGoGame() {
        val goGame = GoGame()
        goGame.initialize()

        for (timeControl in goGame.getTimeControlList()) {
            println(timeControl.getName())
        }

        val timeControl = goGame.getTimeControlList()[0]
        timeControl.initialize()

        val timeControlSettings = timeControl.getSettingList()

        for (timeControlSetting in timeControlSettings) {
            println(timeControlSetting.getLabel())
        }

        goGame.setTimeControl(0)
        val playerA = goGame.getPlayerA()
        val playerB = goGame.getPlayerB()

        val playerATimeControl = playerA!!.getTimerController()
        playerATimeControl.resume()
    }
}