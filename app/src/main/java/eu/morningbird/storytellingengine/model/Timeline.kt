package eu.morningbird.storytellingengine.model

import androidx.lifecycle.MutableLiveData
import eu.morningbird.storytellingengine.model.repository.*
import eu.morningbird.storytellingengine.util.Event
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Timeline {
    val isLoaded: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isFinished: MutableLiveData<Event<Boolean>> = MutableLiveData()

    private var sceneIndex: Int = 0
    private var messageIndex: Int = 0

    var scene: Scene? = null
    var sceneMessages: List<Message>? = ArrayList()

    val messages: List<Message>?
        get() {
            return sceneMessages?.take(messageIndex)
        }

    val chatMessages: List<Message>?
        get() {
            return messages?.filter { !it.fullscreen }
        }

    val currentMessage: Message?
        get() {
            if (!messages.isNullOrEmpty()) return messages?.last()
            return null
        }

    var characters: MutableList<Pair<Character, Position>>? = ArrayList()
    val leftCharacters: List<Pair<Character, Position>>?
        get() {
            return characters?.filter { it.second == Position.LEFT }
        }
    val rightCharacters: List<Pair<Character, Position>>?
        get() {
            return characters?.filter { it.second == Position.RIGHT }
        }

    val changed: MutableLiveData<Event<Pair<Int, Int>>> = MutableLiveData()
    val quickSave: MutableLiveData<Event<Int>> = MutableLiveData()


    fun load(scene: Int = 0, message: Int = 0) {
        GlobalScope.launch {
            PlotRepository.load()

            loadScene(scene)
            loadMessages(scene, message)
            isLoaded.postValue(Event(true))
            isFinished.postValue(Event(false))

            changed.postValue(Event(Pair(scene, message)))
        }
    }

    private suspend fun canAdvanceMessage(scene: Int, message: Int): Boolean {
        val nextMessage = MessageRepository.get(scene, message + 1)
        if (nextMessage != null) return true
        return false
    }

    private suspend fun canAdvanceScene(scene: Int, message: Int): Boolean {
        val nextMessage = MessageRepository.get(scene, message + 1)
        if (nextMessage == null) {
            val nextScene = SceneRepository.get(scene + 1)
            if (nextScene != null) {
                return true
            }
        }
        return false
    }

    private suspend fun advanceMessages(scene: Int, message: Int) {
        loadMessages(scene, message + 1)
    }

    private suspend fun resetMessages(scene: Int) {
        loadMessages(scene, 0)
    }

    private suspend fun loadMessages(scene: Int, message: Int) {
        sceneMessages = MessageRepository.get(scene)
        messageIndex = message
    }

    private suspend fun advanceScene(scene: Int) {
        loadScene(scene + 1)
        resetMessages(scene + 1)
        QuickSave.scene = scene + 1
        quickSave.postValue(Event(scene + 1))
    }

    private suspend fun loadScene(scene: Int) {
        this.scene = SceneRepository.get(scene)
        val present = PresentRepository.get(scene)
        val characters = ArrayList<Pair<Character, Position>>()
        if (present != null) {
            for (relation in present) {
                val character = CharacterRepository.get(relation.character)
                if (character != null) characters.add(Pair(character, relation.position))
            }
        }
        this.characters = characters
        sceneIndex = scene
    }


    fun advance() {
        GlobalScope.launch {
            val scene: Int = sceneIndex
            val message: Int = messageIndex

            if (canAdvanceMessage(scene, message)) {
                advanceMessages(scene, message)
                changed.postValue(Event(Pair(sceneIndex, messageIndex)))
            } else {
                if (canAdvanceScene(scene, message)) {
                    advanceScene(scene)
                    changed.postValue(Event(Pair(sceneIndex, messageIndex)))
                } else {
                    isFinished.postValue(Event(true))
                    isLoaded.postValue(Event(false))
                    QuickSave.scene = 0
                }
            }

        }
    }
}

