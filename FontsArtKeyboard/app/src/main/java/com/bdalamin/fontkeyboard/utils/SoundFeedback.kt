package com.bdalamin.fontkeyboard.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.view.KeyCharacterMap

class SoundFeedback(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0
    private var deleteSoundId: Int = 0
    private var spaceSoundId: Int = 0
    private var returnSoundId: Int = 0

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attrs)
            .build()
        // Use system sounds via AudioManager
    }

    fun playKeyClick() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.5f)
    }

    fun playDelete() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE, 0.5f)
    }

    fun playSpace() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR, 0.5f)
    }

    fun playReturn() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN, 0.5f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
