package eu.morningbird.storytellingengine.viewmodel

import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import eu.morningbird.storytellingengine.model.Settings
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.util.Logger
import eu.morningbird.storytellingengine.view.AboutActivity
import java.text.DecimalFormat
import kotlin.math.roundToInt

class SettingsViewModel : ViewModel() {

    val navigationEvent: MutableLiveData<Event<NavigationDirections>> = MutableLiveData()

    val screenReaderSettingsAlpha: MutableLiveData<Float> =
        if (Settings.screenReader) MutableLiveData(1f) else MutableLiveData(0.5f)
    val soundSettingsAlpha: MutableLiveData<Float> =
        if (Settings.mute) MutableLiveData(0.5f) else MutableLiveData(1f)


    val musicSeekBarProgress: MutableLiveData<Float> = MutableLiveData(readMusicProgress())
    fun saveMusicProgress(progress: Float) {
        Settings.musicVolume = progress / 100f
    }

    private fun readMusicProgress(): Float {
        return Settings.musicVolume * 100f
    }

    val effectsSeekBarProgress: MutableLiveData<Float> = MutableLiveData(readEffectsProgress())
    fun saveEffectsProgress(progress: Float) {
        Settings.effectsVolume = progress / 100f
    }

    private fun readEffectsProgress(): Float {
        return Settings.effectsVolume * 100f
    }

    val textSpeedSeekBarProgress: MutableLiveData<Float> = MutableLiveData(readTextSpeedProgress())
    fun saveTextSpeedProgress(progress: Float) {
        Settings.textAnimationSpeed = (progress / 100f) + 0.5f
    }

    private fun readTextSpeedProgress(): Float {
        return (Settings.textAnimationSpeed - 0.5f) * 100f
    }

    val fontSizeSeekBarProgress: MutableLiveData<Float> = MutableLiveData(readFontSizeProgress())
    fun saveFontSizeProgress(progress: Float) {
        val newSize = ((progress - 1f) * 0.1f) + 0.8f
        val value = DecimalFormat("#.#");
        Settings.fontSize = value.format(newSize).toFloat()
    }

    private fun readFontSizeProgress(): Float {
        val settingsFontSize = Settings.fontSize
        val fontSize = ((settingsFontSize - 0.8f) / 0.1f) + 1f
        val value = DecimalFormat("#.#");
        return value.format(fontSize).toFloat()
    }

    val screenReaderVolumeSeekBarProgress: MutableLiveData<Float> =
        MutableLiveData(readScreenReaderVolumeProgress())

    fun saveScreenReaderVolumeProgress(progress: Float) {
        Settings.screenReaderVolume = progress / 100f
    }

    private fun readScreenReaderVolumeProgress(): Float {
        return Settings.screenReaderVolume * 100f
    }

    val isMuted: MutableLiveData<Boolean> = MutableLiveData(readIsMuted())
    fun saveIsMuted(isMuted: Boolean) {
        Settings.mute = isMuted
    }

    private fun readIsMuted(): Boolean {
        return Settings.mute
    }

    val isScreenReaderEnabled: MutableLiveData<Boolean> =
        MutableLiveData(readIsScreenReaderEnabled())

    fun saveIsScreenReaderEnabled(isMuted: Boolean) {
        Settings.screenReader = isMuted
    }

    private fun readIsScreenReaderEnabled(): Boolean {
        return Settings.screenReader
    }

    val musicOnSeekChange = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams?) {
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            seekBar?.let {
                musicSeekBarProgress.postValue(seekBar.progressFloat)
            }
        }
    }
    val effectsOnSeekChange = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams?) {
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            seekBar?.let {
                effectsSeekBarProgress.postValue(seekBar.progressFloat)
            }
        }
    }
    val textSpeedOnSeekChange = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams?) {
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            seekBar?.let {
                textSpeedSeekBarProgress.postValue(seekBar.progressFloat)
            }
        }
    }
    val fontSizeOnSeekChange = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams?) {
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            seekBar?.let {
                val value = DecimalFormat("#.")
                val newSeekedFontSize = value.format(seekBar.progressFloat).toFloat()
                fontSizeSeekBarProgress.postValue(newSeekedFontSize)
            }
        }
    }

    val screenReaderVolumeOnSeekChange = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams?) {
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            seekBar?.let {
                screenReaderVolumeSeekBarProgress.postValue(seekBar.progressFloat)
            }
        }
    }


    val muteSwitchOnCheckedChange = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isMuted.postValue(isChecked)
        if (isChecked) {
            soundSettingsAlpha.postValue(0.5f)
        } else {
            soundSettingsAlpha.postValue(1f)
        }
    }

    val screenReaderSwitchOnCheckedChange = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isScreenReaderEnabled.postValue(isChecked)
        if (isChecked) {
            screenReaderSettingsAlpha.postValue(1f)
        } else {
            screenReaderSettingsAlpha.postValue(0.5f)
        }
    }

    val aboutOnClick = View.OnClickListener { view ->
        Logger.log("About clicked!", Logger.Level.INFO)
        val intent = Intent(view.context, AboutActivity::class.java)
        navigationEvent.postValue(
            Event(
                NavigationDirections(
                    intent,
                    AboutActivity::class.java,
                    null,
                    null,
                    true
                )
            )
        )
    }
}