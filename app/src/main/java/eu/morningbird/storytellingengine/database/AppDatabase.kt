package eu.morningbird.storytellingengine.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eu.morningbird.storytellingengine.database.dao.*
import eu.morningbird.storytellingengine.model.*

@Database(
    entities = [Character::class, Credit::class, Message::class, Member::class, Plot::class, Present::class, Scene::class, Sprite::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun creditDao(): CreditDao
    abstract fun messageDao(): MessageDao
    abstract fun memberDao(): MemberDao
    abstract fun plotDao(): PlotDao
    abstract fun presentDao(): PresentDao
    abstract fun sceneDao(): SceneDao
    abstract fun spriteDao(): SpriteDao

    suspend fun drop() {
        characterDao().drop()
        messageDao().drop()
        plotDao().drop()
        presentDao().drop()
        sceneDao().drop()
        spriteDao().drop()
    }

    companion object {
        private const val DATABASE_NAME = "application_db"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME
                    )
                        .build()
                }
            }
            return INSTANCE!!
        }
    }

}