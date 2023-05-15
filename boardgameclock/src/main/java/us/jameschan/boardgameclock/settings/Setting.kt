package us.jameschan.boardgameclock.settings

/**
 * A setting item.
 */
class Setting(
    private val label: String,
    defaultValue: String,
    private val type: Type = Type.STRING
) {
    companion object {
        enum class Type {
            OPTIONS, STRING, BOOL;

            companion object {
                fun parse(name: String): Type {
                    return when (name.lowercase()) {
                        "options" -> OPTIONS
                        "string" -> STRING
                        "bool" -> BOOL
                        else -> STRING
                    }
                }
            }

            override fun toString(): String {
                return when (this) {
                    OPTIONS -> "options"
                    STRING -> "string"
                    BOOL -> "bool"
                }
            }
        }
    }

    private val optionList: MutableList<String> = mutableListOf()
    private var value: String = defaultValue
    private var onValueChange: (String) -> Unit = {}
    private var explanation: String = ""
    private var onGetValue: (String) -> String = { it }

    fun getLabel(): String {
        return label
    }

    fun getOptionList(): List<String> {
        return optionList.map(onGetValue)
    }

    fun setValue(value: String) {
        this.value = value
        onValueChange(value)
    }

    fun getValue(): String {
        return onGetValue(value)
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

    fun setOnGetValue(onGetValue: (String) -> String) {
        this.onGetValue = onGetValue
    }

    fun getExplanation(): String {
        return explanation
    }
}