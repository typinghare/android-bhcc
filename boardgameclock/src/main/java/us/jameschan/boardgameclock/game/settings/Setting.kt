package us.jameschan.boardgameclock.game.settings

/**
 * A setting item.
 */
class Setting(
    private val label: String,
    private val optionList: List<String>,
    private val defaultValue: String
) {
    private var value: String = defaultValue

    fun getLabel(): String {
        return label
    }

    fun getOptionList(): List<String> {
        return optionList
    }

    fun setValue(value: String) {
        this.value = value
    }

    fun getValue(): String {
        return value
    }

    fun getDefaultValue(): String {
        return defaultValue
    }
}