package com.bdalamin.fontkeyboard.data.database

import androidx.room.*
import com.bdalamin.fontkeyboard.data.model.ClipboardItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_items ORDER BY isPinned DESC, timestamp DESC LIMIT 50")
    fun getAllItems(): Flow<List<ClipboardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClipboardItem)

    @Delete
    suspend fun delete(item: ClipboardItem)

    @Query("UPDATE clipboard_items SET isPinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: Int, pinned: Boolean)

    @Query("DELETE FROM clipboard_items WHERE isPinned = 0")
    suspend fun clearUnpinned()

    @Query("DELETE FROM clipboard_items")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM clipboard_items")
    suspend fun count(): Int
}
