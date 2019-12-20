package eu.morningbird.storytellingengine.model.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Plot
import eu.morningbird.storytellingengine.model.QuickSave
import eu.morningbird.storytellingengine.model.Settings


object PlotRepository {
    suspend fun load(): Boolean {
        var plot = MyApplication.database.plotDao().get(Settings.Assets.PLAY_SCRIPT_JSON_VERSION)
        if (plot != null) {
            return true
        }
        MyApplication.database.drop()
        if (Settings.Assets.PLAY_SCRIPT_JSON_VERSION_BREAKING) {
            QuickSave.unlocked = 0
            QuickSave.scene = 0
        }
        val mapper = jacksonObjectMapper()
        plot = mapper.readValue(
            MyApplication.instance.assets.open(Settings.Assets.PLAY_SCRIPT_JSON),
            Plot::class.java
        )
        save(plot)
        return true
    }

    private suspend fun save(plot: Plot) {
        plot.characters?.let { characters ->
            for (character in characters) {
                CharacterRepository.save(character)
            }
        }
        plot.scenes?.let { scenes ->
            for ((index, scene) in scenes.withIndex()) {
                SceneRepository.save(scene, index)
            }
        }
        plot.credits?.let { credits ->
            for ((index, credit) in credits.withIndex()) {
                CreditRepository.save(credit, index)
            }
        }
        MyApplication.database.plotDao().insert(plot)
    }

}