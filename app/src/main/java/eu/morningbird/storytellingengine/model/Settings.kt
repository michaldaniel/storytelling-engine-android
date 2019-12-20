package eu.morningbird.storytellingengine.model

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import eu.morningbird.storytellingengine.BuildConfig
import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.util.Event

object Settings {
    val settingsChanged: MutableLiveData<Event<Pair<String, Any>>> = MutableLiveData()

    object Assets {
        const val MESSAGE_SOUND = "sounds/message.ogg"
        const val TITLE_SCREEN_MUSIC = "music/background_01.ogg"
        const val CREDITS_MUSIC = "music/background_01.ogg"
        const val PLAY_SCRIPT_JSON = "play/script.json"
        const val PLAY_SCRIPT_JSON_VERSION = 1
        const val PLAY_SCRIPT_JSON_VERSION_BREAKING = false
    }

    private const val PRIVATE_MODE = 0

    enum class SettingsKeys(val key: String) {
        MUSIC_VOLUME("settings_music_volume"),
        EFFECTS_VOLUME("settings_effects_volume"),
        TEXT_ANIMATION_SPEED("settings_text_animation_speed"),
        FONT_SIZE("settings_font_size"),
        SCREEN_READER("settings_screen_reader"),
        SCREEN_READER_VOLUME("settings_screen_reader_volume"),
        MUTE("settings_mute"),
        ALL("settings_all")
    }

    private var perferences: SharedPreferences? = null


    var musicVolume: Float
        get() {
            if (perferences == null) initPreferences()
            val musicVolume = perferences?.getFloat(SettingsKeys.MUSIC_VOLUME.key, 0.5f)
            if (musicVolume != null) return musicVolume
            return 0.5f
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putFloat(SettingsKeys.MUSIC_VOLUME.key, value)
                apply()
                settingsChanged.postValue(Event(Pair(SettingsKeys.MUSIC_VOLUME.key, value)))
            }
        }

    var effectsVolume: Float
        get() {
            if (perferences == null) initPreferences()
            val effectsVolume = perferences?.getFloat(SettingsKeys.EFFECTS_VOLUME.key, 0.75f)
            if (effectsVolume != null) return effectsVolume
            return 0.75f
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putFloat(SettingsKeys.EFFECTS_VOLUME.key, value)
                apply()
                settingsChanged.postValue(Event(Pair(SettingsKeys.EFFECTS_VOLUME.key, value)))
            }
        }

    var mute: Boolean
        get() {
            if (perferences == null) initPreferences()
            val mute = perferences?.getBoolean(SettingsKeys.MUTE.key, false)
            if (mute != null) return mute
            return false
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putBoolean(SettingsKeys.MUTE.key, value)
                apply()
                settingsChanged.postValue(Event(Pair(SettingsKeys.MUTE.key, value)))
            }
        }

    var textAnimationSpeed: Float
        get() {
            if (perferences == null) initPreferences()
            val textAnimationSpeed =
                perferences?.getFloat(SettingsKeys.TEXT_ANIMATION_SPEED.key, 1f)
            if (textAnimationSpeed != null) return textAnimationSpeed
            return 1f
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putFloat(SettingsKeys.TEXT_ANIMATION_SPEED.key, value)
                apply()
                settingsChanged.postValue(Event(Pair(SettingsKeys.TEXT_ANIMATION_SPEED.key, value)))
            }
        }

    var fontSize: Float
        get() {
            if (perferences == null) initPreferences()
            val fontSize = perferences?.getFloat(SettingsKeys.FONT_SIZE.key, 1f)
            if (fontSize != null) return fontSize
            return 1f
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putFloat(SettingsKeys.FONT_SIZE.key, value)
                apply()
                settingsChanged.postValue(Event(Pair(SettingsKeys.FONT_SIZE.key, value)))
            }
        }

    var screenReader: Boolean
        get() {
            if (perferences == null) initPreferences()
            val screenReader = perferences?.getBoolean(SettingsKeys.SCREEN_READER.key, false)
            if (screenReader != null) return screenReader
            return false
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putBoolean(SettingsKeys.SCREEN_READER.key, value)
                apply()
                settingsChanged.postValue(Event(Pair(SettingsKeys.SCREEN_READER.key, value)))
            }
        }

    var screenReaderVolume: Float
        get() {
            if (perferences == null) initPreferences()
            val screenReaderVolume = perferences?.getFloat(SettingsKeys.SCREEN_READER_VOLUME.key, 0.75f)
            if (screenReaderVolume != null) return screenReaderVolume
            return 0.75f
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putFloat(SettingsKeys.SCREEN_READER_VOLUME.key, value)
                apply()
                settingsChanged.postValue(Event(Pair(SettingsKeys.SCREEN_READER_VOLUME.key, value)))
            }
        }

    fun reset() {
        if (perferences == null) initPreferences()
        perferences?.edit {
            for (settingsKey in SettingsKeys.values()) {
                remove(settingsKey.key)
            }
            apply()
        }
        settingsChanged.postValue(Event(Pair(SettingsKeys.MUSIC_VOLUME.key, this)))
    }

    private fun initPreferences() {
        perferences =
            MyApplication.instance.getSharedPreferences(BuildConfig.APPLICATION_ID, PRIVATE_MODE)
    }
}