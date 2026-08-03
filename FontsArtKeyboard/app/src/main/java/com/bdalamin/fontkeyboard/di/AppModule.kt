package com.bdalamin.fontkeyboard.di

import android.content.Context
import androidx.room.Room
import com.bdalamin.fontkeyboard.data.database.AppDatabase
import com.bdalamin.fontkeyboard.data.database.ClipboardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "fontkeyboard_db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideClipboardDao(db: AppDatabase): ClipboardDao = db.clipboardDao()
}
