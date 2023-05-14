package us.jameschan.boardgameclock.game.settings

/**
 * A setting item.
 */
class Setting(
    private val label: String,
    private val defaultValue: String,
    private val type: Type = Type.STRING
) {
    companion object {
        enum class Type {
            OPTIONS, STRING, BOOL
        }
    }

    private val optionList: MutableList<String> = mutableListOf()
    private var value: String = defaultValue
    private var onValueChange: (String) -> Unit = {}
    private var explanation: String = ""

    init {
        optionList.add(defaultValue)
    }

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

    fun getType(): Type {
        return type
    }

    fun addOption(option: String) {
        optionList.add(option)
    }

    fun setOnValueChange(callback: (String) -> Unit) {
        onValueChange = callback
    }

    fun setExplanation(explanation: String) {
        this.explanation = explanation
    }

    fun getExplanation(): String {
        return explanation
    }
}