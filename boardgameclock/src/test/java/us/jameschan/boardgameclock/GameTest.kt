package us.jameschan.boardgameclock

import org.junit.Test
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.game.games.go.GoGame
import us.jameschan.boardgameclock.util.setInterval
import us.jameschan.boardgameclock.util.setTimeout
import java.util.concurrent.CountDownLatch

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
        val playerA = goGame.getPlayer(Role.A)
        val playerB = goGame.getPlayer(Role.B)

        goGame.start()
        goGame.playerClickEvent(Role.A)

        val latch = CountDownLatch(1)
        setTimeout(1000) {
            goGame.playerClickEvent(Role.A)
        }

        setTimeout(2500) {
            goGame.playerClickEvent(Role.B)
        }

        setTimeout(5000) {
            latch.countDown()
        }

        latch.await()
    }

    @Test
    fun testSetTimeout() {
        val latch = CountDownLatch(3)

        setTimeout(2000) {
            println("2 seconds")
            latch.countDown()
        }

        setTimeout(4000) {
            println("4 seconds")
            latch.countDown()
        }

        latch.await()
    }

    @Test
    fun testSetInterval() {
        val latch = CountDownLatch(50)

        var a = 0
        setInterval(250) {
            a++
            println("What")
            if (a == 40) it.cancel()

            latch.countDown()
        }

        latch.await()
    }
}