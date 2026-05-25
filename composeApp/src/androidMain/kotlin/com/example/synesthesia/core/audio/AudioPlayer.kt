package com.example.synesthesia.core.audio

import android.media.MediaPlayer
import com.example.synesthesia.NoteAIApplication

private var mediaPlayer: MediaPlayer? = null

actual fun playAudio(resName: String) {
    stopAudio()
    val context = NoteAIApplication.instance
    val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
    if (resId != 0) {
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            start()
        }
    }
}

actual fun stopAudio() {
    mediaPlayer?.stop()
    mediaPlayer?.release()
    mediaPlayer = null
}
