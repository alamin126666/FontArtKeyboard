package com.bdalamin.fontkeyboard.data.repository

import com.bdalamin.fontkeyboard.data.database.ClipboardDao
import com.bdalamin.fontkeyboard.data.model.ClipboardItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardRepository @Inject constructor(
    private val dao: ClipboardDao
) {
    val allItems: Flow<List<ClipboardItem>> = dao.getAllItems()

    suspend fun addItem(text: String) {
        dao.insert(ClipboardItem(text = text))
    }

    suspend fun delete(item: ClipboardItem) {
        dao.delete(item)
    }

    suspend fun togglePin(item: ClipboardItem) {
        dao.setPinned(item.id, !item.isPinned)
    }

    suspend fun clearUnpinned() {
        dao.clearUnpinned()
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
