package us.jameschan.boardgameclock;

import us.jameschan.boardgameclock.game.Game

object GameManager {
    private var game: Game? = null

    fun createGame(game: Game): Game {
        this.game = game
        game.initialize()

        return game
    }

    fun getGame(): Game {
        if (game == null) {
            throw RuntimeException("Game has not started yet!")
        }

        return game!!
    }
}
