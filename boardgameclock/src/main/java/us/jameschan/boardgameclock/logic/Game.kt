package us.jameschan.boardgameclock.logic

import kotlin.reflect.KClass

/**
 * An abstract board game.
 */
abstract class Game<G : GameOption, P : PlayerOption>(
    private val gameOption: G,
    private val playerOptionClass: KClass<P>
) : AutoCloseable, OptionContainer<G> {

    private val playerA: Player<P> = Player(playerOptionClass.java.newInstance(), Role.A);

    private val playerB: Player<P> = Player(playerOptionClass.java.newInstance(), Role.B);

    override fun getOption(): G {
        return gameOption
    }

    override fun close() {
    }
}