package us.jameschan.boardgameclock.game.games.go;

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.games.go.timecontrol.YingshiTimeControl
import us.jameschan.boardgameclock.game.games.go.timecontrol.byoyomi.ByoyomiTimeControl

class GoGame : Game() {
    override fun initialize() {
        addTimeControl(ByoyomiTimeControl())
        addTimeControl(YingshiTimeControl())
    }
}
