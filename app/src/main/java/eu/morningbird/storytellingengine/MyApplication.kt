package eu.morningbird.storytellingengine

import android.app.Application
import eu.morningbird.storytellingengine.database.AppDatabase

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        database = AppDatabase.getInstance(applicationContext)
    }
}


/* ------------- GLOBAL TODO -----------------
    TODO: Finish all storyboard activity features
    TODO: Create updater service
    TODO: Create save data model
    TODO: Add auto save feature
    TODO: Create title screen
    TODO: Create settings screen, model and so on
    TODO: Create about screen
    TODO: Create scene selection screen, model and so on
    TODO: Create a flavor with actual story script, assets, application config
 */