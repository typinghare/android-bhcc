package us.jameschan.boardgameclock.logic

class Player<P : PlayerOption>(
    private val playerOption: P,
    private val role: Role
) : OptionContainer<P> {
    override fun getOption(): P {
        return playerOption
    }

    fun getRole(): Role {
        return role
    }
}