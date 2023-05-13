package us.jameschan.boardgameclock.game

import us.jameschan.boardgameclock.game.settings.Settings

open class Game : Settings(), Initializer {
    /**
     * Time control list.
     */
    private val timeControlList: MutableList<TimeControl> = mutableListOf()

    private var playerA: Player? = null
    private var playerB: Player? = null

    /**
     * Initialize this game.
     */
    override fun initialize() {
        addTimeControl(TimeControl("Default", "Default Time control."))
    }

    /**
     * Sets time control.
     */
    fun setTimeControl(timeControlIndex: Int) {
        val timeControl = timeControlList[timeControlIndex]
        val timeControlClass = timeControl.javaClass

        playerA = Player(timeControlClass.newInstance())
        playerB = Player(timeControlClass.newInstance())
    }

    /**
     * Player A.
     */
    fun getPlayerA(): Player? {
        return playerA
    }

    /**
     * Player B.
     */
    fun getPlayerB(): Player? {
        return playerB
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