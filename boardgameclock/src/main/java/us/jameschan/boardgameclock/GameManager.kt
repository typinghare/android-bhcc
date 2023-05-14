package us.jameschan.boardgameclock;

import us.jameschan.boardgameclock.game.Game

object GameManager {
    private var game: Game? = null

    fun createGame(): Game? {
        game = Game()

        return game
    }

    fun getGame(): Game {
        if (game == null) {
            throw RuntimeException("Game has not started yet!")
        }

        return game!!
    }
}
