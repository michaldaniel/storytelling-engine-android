package eu.morningbird.storytellingengine.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.morningbird.storytellingengine.BuildConfig
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.model.QuickSave
import eu.morningbird.storytellingengine.model.Settings
import eu.morningbird.storytellingengine.model.Timeline
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.util.Logger
import eu.morningbird.storytellingengine.util.PlayStoreActions
import eu.morningbird.storytellingengine.view.AboutActivity
import eu.morningbird.storytellingengine.view.ChapterSelectionActivity
import eu.morningbird.storytellingengine.view.SettingsActivity


class TitleScreenViewModel : ViewModel() {


    val navigationEvent: MutableLiveData<Event<NavigationDirections>> = MutableLiveData()
    val startButtonTextLabel: MutableLiveData<Int> = MutableLiveData(R.string.title_new_game)
    val isLoaded: MutableLiveData<Event<Boolean>> = Timeline.isLoaded
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val music: MutableLiveData<String> = MutableLiveData(Settings.Assets.TITLE_SCREEN_MUSIC)

    init {
        lookForSaveData()
    }

    private fun lookForSaveData() {
        if (QuickSave.isAvailable) {
            startButtonTextLabel.postValue(R.string.title_continue)
        }
    }

    fun extrasShareOnClick(view: View) {
        Logger.log("Extras share clicked!", Logger.Level.INFO)
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            view.context.resources.getString(R.string.share_content) + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        view.context.startActivity(sendIntent)
    }

    fun extrasMoreOnClick(view: View) {
        Logger.log("Extras more clicked!", Logger.Level.INFO)
        PlayStoreActions(view.context).openMore()
    }

    fun extrasRateOnClick(view: View) {
        Logger.log("Extras rate clicked!", Logger.Level.INFO)
        PlayStoreActions(view.context).openRate()
    }

    fun extrasAboutOnClick(view: View) {
        Logger.log("Extras about clicked!", Logger.Level.INFO)
        val intent = Intent(view.context, AboutActivity::class.java)
        navigationEvent.postValue(
            Event(
                NavigationDirections(
                    intent,
                    AboutActivity::class.java,
                    null,
                    null,
                    false
                )
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun startButtonOnClick(view: View) {
        Logger.log("Start button clicked!", Logger.Level.INFO)
        isLoading.postValue(true)
        Timeline.load(QuickSave.scene, 0)
    }

    fun chaptersButtonOnClick(view: View) {
        Logger.log("Chapters button clicked!", Logger.Level.INFO)
        val intent = Intent(view.context, ChapterSelectionActivity::class.java)
        navigationEvent.postValue(
            Event(
                NavigationDirections(
                    intent,
                    ChapterSelectionActivity::class.java,
                    null,
                    null,
                    false
                )
            )
        )

    }

    fun settingsButtonOnClick(view: View) {
        Logger.log("Settings button clicked!", Logger.Level.INFO)
        val intent = Intent(view.context, SettingsActivity::class.java)
        navigationEvent.postValue(
            Event(
                NavigationDirections(
                    intent,
                    SettingsActivity::class.java,
                    null,
                    null,
                    false
                )
            )
        )
    }

}