package us.jameschan.boardgameclock.game;

class DefaultTimeControl(
    override val game: Game,
    role: Role?
) : TimeControl(game, role, "Default", "This is a default time control")

