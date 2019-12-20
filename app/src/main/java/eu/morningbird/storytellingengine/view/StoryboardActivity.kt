package eu.morningbird.storytellingengine.view

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.github.florent37.kotlin.pleaseanimate.PleaseAnim
import com.github.florent37.kotlin.pleaseanimate.please
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.adapter.MessagesAdapter
import eu.morningbird.storytellingengine.databinding.ActivityStoryboardBinding
import eu.morningbird.storytellingengine.model.*
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.util.Logger
import eu.morningbird.storytellingengine.util.ScrollControlLinearLayout
import eu.morningbird.storytellingengine.util.clickWithDebounce
import eu.morningbird.storytellingengine.viewmodel.StoryboardViewModel
import kotlinx.android.synthetic.main.activity_storyboard.*
import java.io.FileNotFoundException
import java.util.*
import kotlin.math.roundToInt


class StoryboardActivity : AppCompatActivity() {


    private lateinit var binding: ActivityStoryboardBinding
    private lateinit var viewModel: StoryboardViewModel
    private lateinit var adapter: MessagesAdapter
    private lateinit var sceneEffectsPlayer: MediaPlayer
    private lateinit var backgroundPlayer: MediaPlayer
    private lateinit var messageEffectsPlayer: MediaPlayer
    private lateinit var messageNotificationPlayer: MediaPlayer
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var pulsate: ObjectAnimator
    @Volatile
    var typingLock: Boolean = false
    private val sceneSoundPool: LinkedList<AssetFileDescriptor> = LinkedList()
    private val messageSoundPool: LinkedList<AssetFileDescriptor> = LinkedList()
    private val textToSpeechOnInit = TextToSpeech.OnInitListener { status ->
        if (status != TextToSpeech.ERROR) {
            if (textToSpeech.availableLanguages.contains(Locale.US)) {
                textToSpeech.language = Locale.US
            } else if (textToSpeech.availableLanguages.isNotEmpty()) {
                textToSpeech.language = textToSpeech.availableLanguages.first()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initVars()
        initViews()
        initListeners()
        initObservers()
        initRecyclers()
    }

    override fun onPause() {
        if (sceneEffectsPlayer.isPlaying) sceneEffectsPlayer.pause()
        if (backgroundPlayer.isPlaying) backgroundPlayer.pause()
        if (messageEffectsPlayer.isPlaying) messageEffectsPlayer.pause()
        if (messageNotificationPlayer.isPlaying) messageNotificationPlayer.pause()
        if (textToSpeech.isSpeaking) textToSpeech.stop()
        super.onPause()
    }

    override fun onResume() {
        if (::viewModel.isInitialized) {
            viewModel.scene.value?.let { scene ->
                scene.music?.let { path ->
                    playBackgroundMusic(path)
                }
            }
        }
        typingLock = false
        super.onResume()
    }

    override fun onDestroy() {
        sceneEffectsPlayer.release()
        backgroundPlayer.release()
        messageEffectsPlayer.release()
        messageNotificationPlayer.release()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    private fun initBinding() {
        viewModel = ViewModelProvider(this).get(StoryboardViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_storyboard)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initListeners() {
        sceneEffectsPlayer.setOnCompletionListener {
            playSoundFromQueue(SoundEffectType.SCENE)
        }
        messageEffectsPlayer.setOnCompletionListener {
            playSoundFromQueue(SoundEffectType.MESSAGE)
        }
        storyboardConstraintLayout.clickWithDebounce {
            if (typingLock) return@clickWithDebounce
            if (pulsate.isRunning) pulsate.pause()
            continueImageView.visibility = View.INVISIBLE
            Timeline.advance()

        }
    }

    private fun initViews() {
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar, theme)

        pulsate = ObjectAnimator.ofPropertyValuesHolder(
            continueImageView,
            PropertyValuesHolder.ofFloat("alpha", 0f)
        )
        pulsate.duration = resources.getInteger(R.integer.continue_pulse_frequency).toLong()
        pulsate.repeatCount = ObjectAnimator.INFINITE
        pulsate.repeatMode = ObjectAnimator.REVERSE
        pulsate.start()
    }

    private fun initVars() {
        sceneEffectsPlayer = MediaPlayer()
        messageEffectsPlayer = MediaPlayer()
        messageNotificationPlayer = MediaPlayer()
        backgroundPlayer = MediaPlayer()
        textToSpeech = TextToSpeech(this, textToSpeechOnInit)
    }

    private fun initRecyclers() {
        val linearLayoutManager =
            ScrollControlLinearLayout(
                this, LinearLayoutManager.VERTICAL,
                reversed = false,
                scrollEnabled = false
            )
        chatRecyclerView.addItemDecoration(MessagesAdapter.OverlapDecoration(resources.getInteger(R.integer.message_overlap)))
        chatRecyclerView.layoutManager = linearLayoutManager
        val animator: DefaultItemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }
        chatRecyclerView.itemAnimator = animator
        adapter = MessagesAdapter(this)
        chatRecyclerView.adapter = adapter
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this,
            Observer { event ->
                event.get()?.let { directions -> navigate(directions) }
            }
        )
        viewModel.timelineChanged.observe(this,
            Observer { event ->
                val pair: Pair<Int, Int> = event.get() ?: event.peek()
                adapter.update(pair.first)
                viewModel.update()
            }
        )
        viewModel.stage.observe(this,
            Observer { stage ->
                drawCharactersOnStage(stage)
            })
        viewModel.message.observe(this,
            Observer { message ->
                message?.let {
                    if (it.fullscreen) {
                        showFullscreen(it.text)
                    } else {
                        hideFullscreen()
                        playMessageNotificationSound()
                    }
                    if (Settings.screenReader) it.id?.let { id -> readMessage(it.text, id) }
                } ?: run {
                    hideFullscreen()
                }
            })
        viewModel.scene.observe(this,
            Observer { scene ->
                updateBackground(scene.background)
                scene.music?.let {
                    playBackgroundMusic(it)
                }

            })
        viewModel.sceneSound.observe(this, Observer { path ->
            path?.let {
                addSoundToQueue(it, SoundEffectType.SCENE)
            }
        })
        viewModel.messageSound.observe(this, Observer { path ->
            path?.let {
                addSoundToQueue(it, SoundEffectType.MESSAGE)
            }
        })
        viewModel.typewriterAnimationEvent.observe(this, Observer { event ->
            val typing = event.get()
            typing?.let {
                if (typing) {
                    typingLock = true
                } else {
                    typingLock = false
                    continueImageView.visibility = View.VISIBLE
                    pulsate.start()
                }
            }
        })

        viewModel.savingEvent.observe(this, Observer {
            viewModel.savingIndicatorVisible.postValue(true)
            Handler().postDelayed({ viewModel.savingIndicatorVisible.postValue(false) }, 3000L)
        })

        viewModel.isFinished.observe(this, Observer { event ->
            val finished = event.get()
            if (finished != null && finished) {
                val intent = Intent(this, EndCreditsActivity::class.java)
                viewModel.navigationEvent.postValue(
                    Event(
                        NavigationDirections(
                            intent,
                            EndCreditsActivity::class.java,
                            null,
                            null,
                            true
                        )
                    )
                )
            }
        })

        Settings.settingsChanged.observe(this, Observer { event ->
            val value = event.peek()
            if (value.first == Settings.SettingsKeys.MUSIC_VOLUME.key) {
                backgroundPlayer.setVolume(Settings.musicVolume, Settings.musicVolume)
            }
            if (value.first == Settings.SettingsKeys.EFFECTS_VOLUME.key) {
                sceneEffectsPlayer.setVolume(Settings.effectsVolume, Settings.effectsVolume)
                messageEffectsPlayer.setVolume(Settings.effectsVolume, Settings.effectsVolume)
                messageNotificationPlayer.setVolume(Settings.effectsVolume, Settings.effectsVolume)
            }
            if (value.first == Settings.SettingsKeys.MUTE.key) {
                if (Settings.mute) {
                    sceneEffectsPlayer.setVolume(0f, 0f)
                    messageEffectsPlayer.setVolume(0f, 0f)
                    messageNotificationPlayer.setVolume(0f, 0f)
                    backgroundPlayer.setVolume(0f, 0f)
                } else {
                    sceneEffectsPlayer.setVolume(Settings.effectsVolume, Settings.effectsVolume)
                    messageEffectsPlayer.setVolume(Settings.effectsVolume, Settings.effectsVolume)
                    messageNotificationPlayer.setVolume(
                        Settings.effectsVolume,
                        Settings.effectsVolume
                    )
                    backgroundPlayer.setVolume(Settings.musicVolume, Settings.musicVolume)
                }
            }
        })
    }

    private fun readMessage(text: String, id: Int) {
        val bundle = Bundle()
        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, Settings.screenReaderVolume)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, id.toString())
    }

    private fun playSoundFromQueue(type: SoundEffectType) {
        if (Settings.mute) return
        val player =
            if (type == SoundEffectType.MESSAGE) messageEffectsPlayer else sceneEffectsPlayer
        val pool = if (type == SoundEffectType.MESSAGE) messageSoundPool else sceneSoundPool
        if (pool.isNotEmpty()) {
            val assetFileDescriptor = pool.removeFirst()
            player.reset()
            player.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            if (Settings.mute) {
                player.setVolume(0f, 0f)
            } else {
                player.setVolume(Settings.effectsVolume, Settings.effectsVolume)
            }
            player.prepare()
            player.start()
        }
    }

    private fun playMessageNotificationSound() {
        if (Settings.mute) return
        try {
            val assetFileDescriptor = resources.assets.openFd(Settings.Assets.MESSAGE_SOUND)
            if (messageNotificationPlayer.isPlaying) messageNotificationPlayer.stop()
            messageNotificationPlayer.reset()
            messageNotificationPlayer.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            if (Settings.mute) {
                messageNotificationPlayer.setVolume(0f, 0f)
            } else {
                messageNotificationPlayer.setVolume(Settings.effectsVolume, Settings.effectsVolume)
            }
            messageNotificationPlayer.prepare()
            messageNotificationPlayer.start()
        } catch (e: FileNotFoundException) {
            Logger.log(
                "Missing message sound asset.",
                Logger.Level.DEBUG
            )
            e.printStackTrace()
        }
    }

    private fun playBackgroundMusic(path: String) {
        if (Settings.mute) return
        try {
            val assetFileDescriptor = resources.assets.openFd(path)
            if (backgroundPlayer.isPlaying) sceneEffectsPlayer.stop()
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

    private fun updateBackground(path: String) {
        if (sceneBackgroundImageView.tag == path) return
        sceneBackgroundImageView.tag = path
        try {
            sceneBackgroundImageView.setImageDrawable(
                Drawable.createFromResourceStream(
                    resources,
                    TypedValue(),
                    resources.assets.open(path),
                    null
                )
            )
        } catch (e: FileNotFoundException) {
            Logger.log(Logger.discrepancy_debug, Logger.Level.DEBUG)
            e.printStackTrace()
        }
    }

    private fun addSoundToQueue(path: String, type: SoundEffectType) {
        val player =
            if (type == SoundEffectType.MESSAGE) messageEffectsPlayer else sceneEffectsPlayer
        val pool = if (type == SoundEffectType.MESSAGE) messageSoundPool else sceneSoundPool
        try {
            val assetFileDescriptor = resources.assets.openFd(path)
            pool.addLast(assetFileDescriptor)
            if (!player.isPlaying) {
                playSoundFromQueue(type)
            }
        } catch (e: FileNotFoundException) {
            Logger.log(
                Logger.discrepancy_debug,
                Logger.Level.DEBUG
            )
            e.printStackTrace()
        }
    }

    private fun updateSprite(image: ImageView, sprite: Sprite?, character: Character?) {
        sprite?.let {
            try {
                image.setImageDrawable(
                    Drawable.createFromResourceStream(
                        resources,
                        TypedValue(),
                        resources.assets.open(it.graphic),
                        null
                    )
                )
            } catch (e: FileNotFoundException) {
                Logger.log(
                    Logger.discrepancy_debug,
                    Logger.Level.DEBUG
                )
                e.printStackTrace()
            }
        }
        image.tag = character
    }

    private fun buildCharacterEnterAnimation(
        image: ImageView,
        character: Character?,
        sprite: Sprite?,
        speaker: Position?,
        imagePosition: Position
    ): PleaseAnim {
        return please(duration = 300L) {
            when {
                character == null -> {
                    animate(image) toBe {
                        marginRight(256f)
                        marginLeft(256f)
                        invisible()
                    }
                }
                speaker == imagePosition -> {
                    animate(image) toBe {
                        marginRight(0f)
                        marginLeft(0f)
                        visible()
                    }
                }
                else -> {
                    animate(image) toBe {
                        marginRight(0f)
                        marginLeft(0f)
                        alpha(0.75f)
                    }
                }
            }
            withStartAction {
                updateSprite(image, sprite, character)
            }
        }
    }

    private fun buildCharacterLeaveAnimation(image: ImageView): PleaseAnim {
        return please(duration = 500L) {
            animate(image) toBe {
                marginRight(256f)
                marginLeft(256f)
                invisible()
            }
        }
    }

    private fun drawCharactersOnStage(stage: StoryboardViewModel.Stage) {
        val currentLeftCharacter = leftCharacterImageView.tag as Character?
        val currentRightCharacter = rightCharacterImageView.tag as Character?

        val leftCharacterEnter: PleaseAnim = buildCharacterEnterAnimation(
            leftCharacterImageView,
            stage.leftCharacter,
            stage.leftSprite,
            stage.speaker,
            Position.LEFT
        )
        val rightCharacterEnter: PleaseAnim = buildCharacterEnterAnimation(
            rightCharacterImageView,
            stage.rightCharacter,
            stage.rightSprite,
            stage.speaker,
            Position.RIGHT
        )

        if (currentLeftCharacter != stage.leftCharacter) {
            buildCharacterLeaveAnimation(leftCharacterImageView).start().withEndAction {
                leftCharacterEnter.start()
            }
        } else {
            leftCharacterEnter.start()
        }

        if (currentRightCharacter != stage.rightCharacter) {
            buildCharacterLeaveAnimation(rightCharacterImageView).start().withEndAction {
                rightCharacterEnter.start()
            }
        } else {
            rightCharacterEnter.start()
        }
        adjustSpeakerPosition(stage.speaker)
    }

    private fun adjustSpeakerPosition(position: Position?) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        when (position) {
            Position.RIGHT -> {
                verticalGuideline.setGuidelineBegin(width / 2 - resources.getInteger(R.integer.speaker_adjustment))
            }
            Position.LEFT -> {
                verticalGuideline.setGuidelineBegin(width / 2 + resources.getInteger(R.integer.speaker_adjustment))
            }
            else -> {
                verticalGuideline.setGuidelineBegin(width / 2)
            }
        }
    }

    private fun showFullscreen(text: String) {
        fullscreenSpacingTextView.text = text
        fullscreenTextView.chunk = (6 * Settings.textAnimationSpeed).roundToInt()
        fullscreenTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.fullscreen_message_text_base_size) * Settings.fontSize
        )
        fullscreenSpacingTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.fullscreen_message_text_base_size) * Settings.fontSize
        )
        fullscreenTextView.animateText = text
        chatConstraintLayout.visibility = View.INVISIBLE
        fullscreenMessageConstraintLayout.visibility = View.VISIBLE
    }

    private fun hideFullscreen() {
        chatConstraintLayout.visibility = View.VISIBLE
        fullscreenMessageConstraintLayout.visibility = View.INVISIBLE
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
            lifecycleOwner(this@StoryboardActivity)
            title(R.string.dialog_exit_to_menu_title)
            message(R.string.dialog_exit_to_menu_message)
            positiveButton(R.string.exit) { dialog ->
                val intent = Intent(dialog.context, TitleScreenActivity::class.java)
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
                dialog.dismiss()
            }
            negativeButton(R.string.cancel)
        }
    }

    override fun startActivity(intent: Intent?, bundle: Bundle?) {
        super.startActivity(intent, bundle)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


    private enum class SoundEffectType {
        MESSAGE, SCENE
    }

}
