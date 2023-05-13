package us.jameschan.boardgameclock.game

import us.jameschan.boardgameclock.game.settings.Settings

/**
 * A game has several time controls, which can be registered by `addTimeControl` method. This should
 * be written in the override `initialize` method.
 * @see TimeControl
 */
open class Game : Settings(), Initializer {
    /**
     * Time control list.
     */
    private val timeControlList: MutableList<TimeControl> = mutableListOf()

    private val playerMap: MutableMap<Role, Player> = mutableMapOf()

    private var gameStarted: Boolean = false

    private var currentPlayerRole: Role? = null

    /**
     * Initialize this game.
     */
    override fun initialize() {
        addTimeControl(TimeControl(this, "Default", "Default Time control."))
    }

    /**
     * Sets time control. Players are initialized after setting a time control.
     */
    fun setTimeControl(timeControlIndex: Int) {
        val timeControl = timeControlList[timeControlIndex]
        val timeControlClass = timeControl.javaClass

        playerMap[Role.A] =
            Player(timeControlClass.getConstructor(Game::class.java).newInstance(this))
        playerMap[Role.B] =
            Player(timeControlClass.getConstructor(Game::class.java).newInstance(this))
    }

    /**
     * Gets a player.
     */
    fun getPlayer(role: Role): Player {
        if (!playerMap.containsKey(role)) {
            throw RuntimeException("Player does not exist. Please set time control first.")
        }

        return playerMap[role]!!
    }

    /**
     * Starts this game.
     */
    fun start() {
        gameStarted = true
    }

    /**
     * Player clicks self-section.
     */
    fun playerClickEvent(role: Role) {
        if (!gameStarted) {
            throw RuntimeException("Game has not been started yet.")
        }

        val theOtherRole = if (role == Role.A) Role.B else Role.A

        if (currentPlayerRole == null) {
            // First click.
            currentPlayerRole = theOtherRole
            getPlayer(theOtherRole).getTimerController().resume()
        } else {
            if (currentPlayerRole == role) {
                // Switch to the other role.
                currentPlayerRole = theOtherRole
                getPlayer(role).getTimerController().pause()
                getPlayer(theOtherRole).getTimerController().resume()
            }
        }
    }

    /**
     * Returns time control list.
     */
    fun getTimeControlList(): List<TimeControl> {
        return timeControlList
    }

    /**
     * Adds a time control.
     */
    protected fun addTimeControl(timeControl: TimeControl) {
        timeControlList.add(timeControl)
    }
}