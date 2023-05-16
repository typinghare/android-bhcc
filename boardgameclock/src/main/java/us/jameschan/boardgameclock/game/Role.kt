package us.jameschan.boardgameclock.game

enum class Role {
    A, B;

    override fun toString(): String {
        return if (this == A) "A" else "B"
    }
}