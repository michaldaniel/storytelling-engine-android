package eu.morningbird.storytellingengine.model

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import eu.morningbird.storytellingengine.BuildConfig
import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.util.Event

object QuickSave {
    private const val PRIVATE_MODE = 0
    private const val KEY_SCENE = "quick_save_scene"
    private const val KEY_UNLOCKED = "quick_save_unlocked"
    private var perferences: SharedPreferences? = null

    val savingEvent: MutableLiveData<Event<Int>> = MutableLiveData()


    val isAvailable: Boolean
        get() {
            if (perferences == null) initPreferences()
            val scene = perferences?.getInt(KEY_SCENE, 0)
            if (scene != 0) return true
            return false
        }

    var scene: Int
        get() {
            if (perferences == null) initPreferences()
            val scene = perferences?.getInt(KEY_SCENE, 0)
            if (scene != null) return scene
            return 0
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putInt(KEY_SCENE, value)
                apply()
                savingEvent.postValue(Event(value))
            }
            if (value > unlocked) {
                unlocked = value
            }
        }

    var unlocked: Int
        get() {
            if (perferences == null) initPreferences()
            val scene = perferences?.getInt(KEY_UNLOCKED, 0)
            if (scene != null) return scene
            return 0
        }
        set(value) {
            if (perferences == null) initPreferences()
            perferences?.edit {
                putInt(KEY_UNLOCKED, value)
                apply()
                savingEvent.postValue(Event(value))
            }
        }

    private fun initPreferences() {
        perferences =
            MyApplication.instance.getSharedPreferences(BuildConfig.APPLICATION_ID, PRIVATE_MODE)
    }
}