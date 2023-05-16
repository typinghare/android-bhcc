package us.jameschan.boardgameclock.game

import us.jameschan.boardgameclock.settings.Settings

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

    private var clockStopRole: Role? = null

    /**
     * Initialize this game.
     */
    override fun initialize() {
        addTimeControl(DefaultTimeControl(this, null))
    }

    /**
     * Sets time control. Players are initialized after setting a time control.
     */
    fun setTimeControl(timeControlIndex: Int) {
        val timeControl = timeControlList[timeControlIndex]
        val timeControlClass = timeControl.javaClass

        val targetConstructor = timeControlClass.getConstructor(Game::class.java, Role::class.java)

        playerMap[Role.A] =
            Player(targetConstructor.newInstance(this, Role.A))
        playerMap[Role.B] =
            Player(targetConstructor.newInstance(this, Role.B))
    }

    fun setTimeControl(timeControlName: String) {
        for (i in timeControlList.indices) {
            if (timeControlList[i].getName() == timeControlName) {
                setTimeControl(i)
                break
            }
        }
    }

    fun clockStop(role: Role) {
        clockStopRole = role
    }

    fun isClockStop(): Boolean {
        return clockStopRole != null
    }

    fun clockStopRole(): Role {
        return clockStopRole!!
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

        for (player in playerMap.values) {
            player.initTimerController()
            player.getTimerController().initializeTimer()
        }
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