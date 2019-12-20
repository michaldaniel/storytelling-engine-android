package eu.morningbird.storytellingengine.view

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.github.florent37.kotlin.pleaseanimate.PleaseAnim
import com.github.florent37.kotlin.pleaseanimate.please
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.databinding.ActivityEndCreditsBinding
import eu.morningbird.storytellingengine.model.Credit
import eu.morningbird.storytellingengine.model.Member
import eu.morningbird.storytellingengine.model.Settings
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.util.Logger
import eu.morningbird.storytellingengine.util.PlayStoreActions
import eu.morningbird.storytellingengine.viewmodel.EndCreditsViewModel
import kotlinx.android.synthetic.main.activity_end_credits.*
import java.io.FileNotFoundException


class EndCreditsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEndCreditsBinding
    private lateinit var viewModel: EndCreditsViewModel
    private lateinit var backgroundPlayer: MediaPlayer

    private var canAdvance: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVars()
        initBinding()
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
        viewModel = ViewModelProvider(this).get(EndCreditsViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_credits)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViews() {
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar, theme)
        canAdvance = false
        creditsConstraintLayout.setOnClickListener {
            if (this.canAdvance) viewModel.advance()
        }

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
        viewModel.credit.observe(this, Observer { credit ->
            credit?.let {
                if (creditsTextView.alpha != 0f) {
                    buildClearAnimation().start().withEndAction {
                        buildCreditAnimation(credit).start()
                    }
                } else {
                    buildCreditAnimation(credit).start()
                }
            }
        })
        viewModel.isFinished.observe(this, Observer { event ->
            val value = event.get()
            value?.let {
                if (value) {
                    if (creditsTextView.alpha != 0f) {
                        buildClearAnimation().start().withEndAction {
                            showFinishedDialog()
                        }
                    }
                }
            }
        })
        viewModel.showThankYou.observe(this, Observer { event ->
            val value = event.get()
            value?.let {
                if (value) {
                    if (creditsTextView.alpha != 0f) {
                        buildClearAnimation().start().withEndAction {
                            buildCreditAnimation(this.getString(R.string.credits_thank_you)).start()
                        }
                    } else {
                        buildCreditAnimation(this.getString(R.string.credits_thank_you)).start()
                    }
                }
            }
        })
        viewModel.showEngineInfo.observe(this, Observer { event ->
            val value = event.get()
            value?.let {
                if (value) {
                    if (creditsTextView.alpha != 0f) {
                        buildClearAnimation().start().withEndAction {
                            buildCreditAnimation(this.getString(R.string.credits_engine_info)).start()
                        }
                    } else {
                        buildCreditAnimation(this.getString(R.string.credits_engine_info)).start()
                    }
                }
            }
        })
        viewModel.isLoaded.observe(this, Observer { event ->
            val value = event.get()
            value?.let {
                if (value) {
                    loadingProgressBar.visibility = View.GONE
                    viewModel.advance()
                    canAdvance = true
                }
            }
        })
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

    private fun showFinishedDialog() {
        val myItems = listOf(
            this.getString(R.string.dialogs_credits_finished_review),
            this.getString(R.string.dialogs_credits_finished_more),
            this.getString(R.string.dialogs_credits_finished_exit)
        )
        MaterialDialog(this).show {
            lifecycleOwner(this@EndCreditsActivity)
            title(R.string.dialogs_credits_finished_title)
            message(R.string.dialogs_credits_finished_message)
            listItems(items = myItems, selection = object : ItemListener {
                override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                    when (index) {
                        0 -> {
                            dialog.dismiss()
                            PlayStoreActions(dialog.context).openRate()
                        }
                        1 -> {
                            dialog.dismiss()
                            PlayStoreActions(dialog.context).openMore()
                        }
                        2 -> {
                            dialog.dismiss()
                        }
                    }
                }
            })
            onDismiss { dialog ->
                goToTitleScreen(dialog.context)
            }
            noAutoDismiss()
        }
    }

    private fun goToTitleScreen(context: Context) {
        val intent = Intent(context, TitleScreenActivity::class.java)
        viewModel.navigationEvent.postValue(
            Event(
                NavigationDirections(
                    intent,
                    TitleScreenActivity::class.java,
                    null,
                    null,
                    true
                )
            )
        )
    }

    private fun buildClearAnimation(): PleaseAnim {
        return please(duration = 300L) {
            animate(headerTextView) toBe {
                invisible()
            }
            animate(headerSeparator) toBe {
                invisible()
            }
            animate(creditsTextView) toBe {
                invisible()
            }
        }
    }

    private fun buildCreditAnimation(credit: String): PleaseAnim {
        return please(duration = 300L) {
            animate(creditsTextView) toBe {
                visible()
            }

            withStartAction {
                creditsTextView.text = credit
            }
        }
    }

    private fun buildCreditAnimation(credit: Pair<Credit, List<Member>>): PleaseAnim {
        return please(duration = 300L) {
            animate(headerTextView) toBe {
                visible()
            }
            animate(headerSeparator) toBe {
                visible()
            }
            animate(creditsTextView) toBe {
                visible()
            }

            withStartAction {
                headerTextView.text = credit.first.name
                val stringBuilder = StringBuilder()
                for ((index, member) in credit.second.withIndex()) {
                    stringBuilder.append(member.name)
                    if (index != credit.second.size - 1) stringBuilder.append(System.getProperty("line.separator"))
                }
                creditsTextView.text = stringBuilder.toString()
            }
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
        MaterialDialog(this).show {
            lifecycleOwner(this@EndCreditsActivity)
            title(R.string.dialog_exit_to_menu_title)
            message(R.string.dialog_exit_to_menu_message)
            positiveButton(R.string.exit) { dialog ->
                goToTitleScreen(dialog.context)
                dialog.dismiss()
            }
            negativeButton(R.string.cancel)
        }
    }

    override fun startActivity(intent: Intent?, bundle: Bundle?) {
        super.startActivity(intent, bundle)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
