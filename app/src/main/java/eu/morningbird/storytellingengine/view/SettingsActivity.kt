package eu.morningbird.storytellingengine.view

import android.content.Intent
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.databinding.ActivitySettingsBinding
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()
        initObservers()
    }

    private fun initBinding() {
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViews() {
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar, theme)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this,
            Observer { event ->
                event.get()?.let { directions -> navigate(directions) }
            }
        )
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val params = toolbar.layoutParams as ConstraintLayout.LayoutParams
            val statusBarHeight = insets.systemWindowInsetTop
            params.setMargins(
                0,
                statusBarHeight,
                0,
                0
            )
            toolbar.layoutParams = params
            insets
        }

        viewModel.musicSeekBarProgress.observe(this, Observer { progress ->
            viewModel.saveMusicProgress(progress)
            settingsMusicIndicatorSeekBar.setProgress(progress)
        })
        viewModel.effectsSeekBarProgress.observe(this, Observer { progress ->
            viewModel.saveEffectsProgress(progress)
            settingsEffectsIndicatorSeekBar.setProgress(progress)
        })
        viewModel.textSpeedSeekBarProgress.observe(this, Observer { progress ->
            viewModel.saveTextSpeedProgress(progress)
            settingsTextSpeedIndicatorSeekBar.setProgress(progress)
        })
        viewModel.fontSizeSeekBarProgress.observe(this, Observer { progress ->
            viewModel.saveFontSizeProgress(progress)
            settingsFontSizeIndicatorSeekBar.setProgress(progress)
        })
        viewModel.screenReaderVolumeSeekBarProgress.observe(this, Observer { progress ->
            viewModel.saveScreenReaderVolumeProgress(progress)
            settingsScreenReaderVolumeIndicatorSeekBar.setProgress(progress)
        })
        viewModel.isMuted.observe(this, Observer { isMuted ->
            viewModel.saveIsMuted(isMuted)
            settingsMuteSwitch.isChecked = isMuted
        })
        viewModel.isScreenReaderEnabled.observe(this, Observer { isScreenReaderEnabled ->
            viewModel.saveIsScreenReaderEnabled(isScreenReaderEnabled)
            settingsScreenReaderSwitch.isChecked = isScreenReaderEnabled
        })
        settingsMusicIndicatorSeekBar.onSeekChangeListener = viewModel.musicOnSeekChange
        settingsEffectsIndicatorSeekBar.onSeekChangeListener = viewModel.effectsOnSeekChange
        settingsTextSpeedIndicatorSeekBar.onSeekChangeListener = viewModel.textSpeedOnSeekChange
        settingsFontSizeIndicatorSeekBar.onSeekChangeListener = viewModel.fontSizeOnSeekChange
        settingsScreenReaderVolumeIndicatorSeekBar.onSeekChangeListener =
            viewModel.screenReaderVolumeOnSeekChange
        settingsMuteSwitch.setOnCheckedChangeListener(viewModel.muteSwitchOnCheckedChange)
        settingsScreenReaderSwitch.setOnCheckedChangeListener(viewModel.screenReaderSwitchOnCheckedChange)
        settingsAboutImageView.setOnClickListener(viewModel.aboutOnClick)
        settingsAboutTextView.setOnClickListener(viewModel.aboutOnClick)
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
