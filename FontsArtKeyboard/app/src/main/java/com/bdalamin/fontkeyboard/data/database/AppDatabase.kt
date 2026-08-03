package com.bdalamin.fontkeyboard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bdalamin.fontkeyboard.data.model.ClipboardItem

@Database(
    entities = [ClipboardItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clipboardDao(): ClipboardDao
}
