package com.example.zshopfinal.Service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val source = event?.source
        if (source != null) {
            val contentDescription = source.contentDescription
            if (!contentDescription.isNullOrBlank()) {
                // Speak the content description to the user
                val utterance = contentDescription.toString()
                val speech = TextToSpeech(this, null)
                speech.speak(utterance, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }
    override fun onInterrupt() {
        // This method is called when the AccessibilityService is interrupted.
    }
}

