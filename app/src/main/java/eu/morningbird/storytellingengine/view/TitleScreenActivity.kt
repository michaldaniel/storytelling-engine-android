package eu.morningbird.storytellingengine.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.databinding.ActivityTitleScreenBinding
import eu.morningbird.storytellingengine.model.Settings
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.util.Logger
import eu.morningbird.storytellingengine.viewmodel.TitleScreenViewModel
import kotlinx.android.synthetic.main.activity_title_screen.*
import java.io.FileNotFoundException


class TitleScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTitleScreenBinding
    private lateinit var viewModel: TitleScreenViewModel
    private lateinit var backgroundPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initVars()
        initViews()
        initObservers()
    }

    override fun onPause() {
        if (backgroundPlayer.isPlaying) backgroundPlayer.pause()
        super.onPause()
    }

    override fun onResume() {
        if (::viewModel.isInitialized) {
            viewModel.music.value?.let { music ->
                playBackgroundMusic(music)
            }
        }
        super.onResume()
    }

    override fun onDestroy() {
        backgroundPlayer.release()
        super.onDestroy()
    }

    private fun initBinding() {
        viewModel = ViewModelProvider(this).get(TitleScreenViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_title_screen)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViews() {
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar, theme)
    }

    private fun initVars() {
        backgroundPlayer = MediaPlayer()
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this,
            Observer { event ->
                event.get()?.let { directions -> navigate(directions) }
            }
        )
        viewModel.isLoaded.observe(this, Observer { loadEvent ->
            val isLoaded: Boolean = loadEvent.get() ?: false
            viewModel.isLoading.postValue(false)
            if (isLoaded) {
                val intent = Intent(this, StoryboardActivity::class.java)
                viewModel.navigationEvent.postValue(
                    Event(
                        NavigationDirections(
                            intent,
                            StoryboardActivity::class.java,
                            null,
                            null,
                            false
                        )
                    )
                )
            }
        })
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val params = titleScreenConstraintLayout.layoutParams as ConstraintLayout.LayoutParams
            val statusBarHeight = insets.systemWindowInsetTop
            params.setMargins(0, statusBarHeight, 0, 0)
            titleScreenConstraintLayout.layoutParams = params
            insets
        }

        Settings.settingsChanged.observe(this, Observer { event ->
            val value = event.peek()
            if (value.first == Settings.SettingsKeys.MUSIC_VOLUME.key) {
                backgroundPlayer.setVolume(Settings.musicVolume, Settings.musicVolume)
            }
            if (value.first == Settings.SettingsKeys.MUTE.key) {
                if (Settings.mute) backgroundPlayer.setVolume(0f, 0f)
            }
        })
    }

    private fun playBackgroundMusic(path: String) {
        try {
            val assetFileDescriptor = resources.assets.openFd(path)
            if (backgroundPlayer.isPlaying) backgroundPlayer.stop()
            backgroundPlayer.reset()
            backgroundPlayer.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            backgroundPlayer.isLooping = true
            if (Settings.mute) {
                backgroundPlayer.setVolume(0f, 0f)
            } else {
                backgroundPlayer.setVolume(Settings.musicVolume, Settings.musicVolume)
            }
            backgroundPlayer.prepare()
            backgroundPlayer.start()
        } catch (e: FileNotFoundException) {
            Logger.log(
                Logger.discrepancy_debug,
                Logger.Level.DEBUG
            )
            e.printStackTrace()
        }
    }

    private fun navigate(directions: NavigationDirections) {
        val intent = Intent(this, directions.destination)
        directions.flags?.let {
            for (flag in it) intent.addFlags(flag)
        }
        directions.bundle?.let {
            intent.putExtras(it)
        }
        startActivity(intent)
        if (directions.shouldFinishCurrentActivity) this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun startActivity(intent: Intent?, bundle: Bundle?) {
        super.startActivity(intent, bundle)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
