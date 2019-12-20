package eu.morningbird.storytellingengine.viewmodel

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.model.Scene
import eu.morningbird.storytellingengine.model.Timeline
import eu.morningbird.storytellingengine.model.repository.PlotRepository
import eu.morningbird.storytellingengine.model.repository.SceneRepository
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.adapter.ChaptersAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChapterSelectionViewModel : ViewModel() {
    val navigationEvent: MutableLiveData<Event<NavigationDirections>> = MutableLiveData()
    val isLoaded: MutableLiveData<Boolean> = MutableLiveData(false)
    val isTimelineLoaded: MutableLiveData<Event<Boolean>> = Timeline.isLoaded
    val chapters: MutableLiveData<List<Scene>> = MutableLiveData()

    val onChapterClickListener = object : ChaptersAdapter.OnItemCliclListener {
        override fun onClick(view: View, scene: Scene) {
            scene.order?.let { order ->
                MaterialDialog(view.context).show {
                    lifecycleOwner(view.context as LifecycleOwner)
                    title(R.string.dialog_load_chapter_title)
                    message(R.string.dialog_load_chapter_message)
                    positiveButton(R.string.rewind) { dialog ->
                        isLoaded.postValue(false)
                        Timeline.load(order, 0)
                        dialog.dismiss()
                    }
                    negativeButton(R.string.cancel)
                }

            }
        }
    }

    init {
        GlobalScope.launch {
            if (PlotRepository.load()) {
                val chapters = SceneRepository.getChapters()
                if (!chapters.isNullOrEmpty()) this@ChapterSelectionViewModel.chapters.postValue(
                    chapters
                )
                isLoaded.postValue(true)
            }
        }
    }

}