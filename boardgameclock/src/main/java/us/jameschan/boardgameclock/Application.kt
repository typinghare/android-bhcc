package us.jameschan.boardgameclock

import android.util.Log
import com.android.volley.RequestQueue
import us.jameschan.boardgameclock.dto.UserSettingsDto
import us.jameschan.boardgameclock.settings.Setting
import us.jameschan.boardgameclock.settings.SettingManager
import us.jameschan.boardgameclock.settings.Settings

object Application : Settings() {
    init {
        refreshSettings()
    }

    fun getSettings(): List<Setting> {
        return mutableListOf(
            SettingManager.getSetting("us.jameschan.boardgameclock.Application.language")!!,
            SettingManager.getSetting("us.jameschan.boardgameclock.Application.clickingSoundEffect")!!,
            SettingManager.getSetting("us.jameschan.boardgameclock.Application.warningSoundEffect")!!
        )
    }

    fun refreshSettings() {
        clearAllSettings()

        val languageSetting =
            Setting("Language", LocalUser.language, Setting.Companion.Type.OPTIONS).apply {
                addOption("english")
                addOption("chinese")
                addOption("japanese")
                setOnValueChange {
                    val lang = when (it) {
                        "English" -> "english"
                        "简体中文" -> "chinese"
                        "日本語" -> "japanese"
                        else -> ""
                    }

                    LocalUser.language = lang
                }

                setOnGetValue {
                    when (it) {
                        "english" -> "English"
                        "chinese" -> "简体中文"
                        "japanese" -> "日本語"
                        else -> ""
                    }
                }
            }
        addSetting(languageSetting)
        SettingManager.setSetting(
            "us.jameschan.boardgameclock.Application.language",
            languageSetting
        )

        val clickingSoundEffectText = Lang.translate("Clicking Sound Effect")
        val clickingSoundEffectSetting =
            Setting(
                clickingSoundEffectText,
                LocalUser.clickingSoundEffect.toString(),
                Setting.Companion.Type.BOOL
            ).apply {
                val explanation =
                    "The clock will emit a beep sound whenever players tap the screen."
                setExplanation(Lang.translate(explanation))

                setOnValueChange {
                    LocalUser.clickingSoundEffect = it.toBoolean()
                }
            }
        addSetting(clickingSoundEffectSetting)
        SettingManager.setSetting(
            "us.jameschan.boardgameclock.Application.clickingSoundEffect",
            clickingSoundEffectSetting
        )

        val warningSoundEffectText = Lang.translate("Warning Sound Effect")
        val warningSoundEffectSetting =
            Setting(
                warningSoundEffectText,
                LocalUser.warningSoundEffect.toString(),
                Setting.Companion.Type.BOOL
            ).apply {
                val explanation = "The clock will emit a beep sound as a warning to players " +
                        "when there are only five minutes remaining."
                setExplanation(Lang.translate(explanation))

                setOnValueChange {
                    LocalUser.warningSoundEffect = it.toBoolean()
                }
            }
        addSetting(warningSoundEffectSetting)
        SettingManager.setSetting(
            "us.jameschan.boardgameclock.Application.warningSoundEffect",
            warningSoundEffectSetting
        )
    }

    /**
     * Persists settings.
     */
    fun persistSettings(requestQueue: RequestQueue) {
        if (LocalUser.userId == null) {
            return
        }

        val userSettingsDto = UserSettingsDto(
            LocalUser.userId!!,
            LocalUser.language,
            LocalUser.clickingSoundEffect,
            LocalUser.warningSoundEffect
        )

        requestQueue.add(Api.updateUserSettings(LocalUser.userId!!, userSettingsDto, {
            LocalUser.settings(it)
        }, {
            Log.d("PersistSettings", it.message.toString())
        }))
    }
}