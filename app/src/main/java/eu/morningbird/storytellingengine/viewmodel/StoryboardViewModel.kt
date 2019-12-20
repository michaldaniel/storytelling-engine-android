package eu.morningbird.storytellingengine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.morningbird.storytellingengine.model.*
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.view.TypeWriterTextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StoryboardViewModel : ViewModel() {


    val navigationEvent: MutableLiveData<Event<NavigationDirections>> = MutableLiveData()
    var timelineChanged: MutableLiveData<Event<Pair<Int, Int>>> = Timeline.changed
    val stage: MutableLiveData<Stage> = MutableLiveData()
    val message: MutableLiveData<Message?> = MutableLiveData()
    val scene: MutableLiveData<Scene> = MutableLiveData()
    val sceneSound: MutableLiveData<String> = MutableLiveData()
    val messageSound: MutableLiveData<String> = MutableLiveData()
    val typewriterAnimationEvent: MutableLiveData<Event<Boolean>> = TypeWriterTextView.typingEvent
    val savingIndicatorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val savingEvent: MutableLiveData<Event<Int>> = QuickSave.savingEvent
    val isFinished: MutableLiveData<Event<Boolean>> = Timeline.isFinished

    private val speakerPosition: Position?
        get() {
            Timeline.currentMessage?.let { message ->
                val leftCharacter =
                    Timeline.leftCharacters?.find { it.first.name == message.characterName }
                if (leftCharacter != null) {
                    return Position.LEFT
                } else {
                    val rightCharacter =
                        Timeline.rightCharacters?.find { it.first.name == message.characterName }
                    if (rightCharacter != null) {
                        return Position.RIGHT
                    }
                }
                return Position.NONE
            }
            return null
        }

    init {
        val isLoaded = Timeline.isLoaded.value?.peek()
        if (isLoaded == null || !isLoaded) {
            Timeline.load(QuickSave.scene, 0)
        }
    }

    private fun updateMessage() {
        Timeline.currentMessage.let { message ->
            if (message != this@StoryboardViewModel.message.value) {
                message?.let {
                    if (it.sound != null) {
                        messageSound.postValue(it.sound)
                    }
                    if (message == Timeline.sceneMessages?.last()) {
                        sceneSound.postValue(this@StoryboardViewModel.scene.value?.outro)
                    }
                }
                this@StoryboardViewModel.message.postValue(message)
            }
        }
    }

    private fun updateScene() {
        Timeline.scene?.let { scene ->
            if (scene != this@StoryboardViewModel.scene.value) {
                this@StoryboardViewModel.scene.postValue(scene)
                if (scene.intro != null) sceneSound.postValue(scene.intro)

            }
        }
    }

    private suspend fun updateStage() {
        val stage = Stage()
        var leftMessage: Message? = null
        var rightMessage: Message? = null
        Timeline.chatMessages?.let { chatMessages ->
            leftMessage = chatMessages.findLast { message ->
                Timeline.leftCharacters?.find { it.first.name == message.characterName } != null
            }
            rightMessage = chatMessages.findLast { message ->
                Timeline.rightCharacters?.find { it.first.name == message.characterName } != null
            }
        }
        if (leftMessage == null) {
            Timeline.sceneMessages?.let { sceneMessages ->
                leftMessage = sceneMessages.find { message ->
                    !message.fullscreen && Timeline.leftCharacters?.find { it.first.name == message.characterName } != null
                }
            }
        }
        if (rightMessage == null) {
            Timeline.sceneMessages?.let { sceneMessages ->
                rightMessage = sceneMessages.find { message ->
                    !message.fullscreen && Timeline.rightCharacters?.find { it.first.name == message.characterName } != null
                }
            }
        }
        stage.leftCharacter = leftMessage?.fetchCharacter()
        stage.leftSprite = leftMessage?.fetchSprite()
        stage.rightCharacter = rightMessage?.fetchCharacter()
        stage.rightSprite = rightMessage?.fetchSprite()

        stage.speaker = speakerPosition

        this@StoryboardViewModel.stage.postValue(stage)
    }

    fun update() {
        updateScene()
        updateMessage()
        GlobalScope.launch {
            updateStage()
        }
    }

    class Stage {
        var leftCharacter: Character? = null
        var rightCharacter: Character? = null
        var leftSprite: Sprite? = null
        var rightSprite: Sprite? = null
        var speaker: Position? = null
    }
}