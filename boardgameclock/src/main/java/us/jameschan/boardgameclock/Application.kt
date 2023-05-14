package us.jameschan.boardgameclock

import us.jameschan.boardgameclock.game.settings.Setting
import us.jameschan.boardgameclock.game.settings.Settings

object Application : Settings() {
    init {
        refresh()
    }

    private fun refresh() {
        clearAllSettings()

        addSetting(Setting("Language", "English", Setting.Companion.Type.OPTIONS).apply {
            addOption("简体中文")
            addOption("日本語")
            setOnValueChange {
                val lang = when (it) {
                    "English" -> "english"
                    "简体中文" -> "chinese"
                    "日本語" -> "japanese"
                    else -> ""
                }

                LocalUser.language = lang
            }
        })

        val clickingSoundEffectText = Lang.translate("Clicking Sound Effect")
        addSetting(Setting(clickingSoundEffectText, "true", Setting.Companion.Type.BOOL).apply {
            val explanation =
                "The clock will emit a beep sound whenever players tap the screen."
            setExplanation(Lang.translate(explanation))

            setOnValueChange {
                LocalUser.clickingSoundEffect = it.toBoolean()
            }
        })

        val warningSoundEffectText = Lang.translate("Warning Sound Effect")
        addSetting(Setting(warningSoundEffectText, "true", Setting.Companion.Type.BOOL).apply {
            val explanation = "The clock will emit a beep sound as a warning to players " +
                    "when there are only five minutes remaining."
            setExplanation(Lang.translate(explanation))

            setOnValueChange {
                LocalUser.warningSoundEffect = it.toBoolean()
            }
        })
    }
}