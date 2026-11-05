package com.example.keyboard.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.math.sin

class KeyboardFeedback(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val audioManager: AudioManager? by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    // Synthesized iOS-like click sound buffer for ultra-low latency & zero external asset issues
    private var clickAudioTrack: AudioTrack? = null

    init {
        try {
            val sampleRate = 44100
            val durationMs = 12
            val numSamples = (sampleRate * durationMs) / 1000
            val sample = ShortArray(numSamples)

            // High frequency soft wood-tap click characteristic of iOS keyboard
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Decaying sine wave burst with frequency ~2200Hz dropping to 800Hz
                val freq = 2200.0 * (1.0 - (i.toDouble() / numSamples) * 0.6)
                val decay = (1.0 - (i.toDouble() / numSamples))
                val env = decay * decay
                val wave = sin(2.0 * Math.PI * freq * t) * env
                sample[i] = (wave * 26000).toInt().toShort()
            }

            clickAudioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(sample.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build().apply {
                    write(sample, 0, sample.size)
                }
        } catch (_: Exception) {
            clickAudioTrack = null
        }
    }

    fun playClick(soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            clickAudioTrack?.let {
                it.stop()
                it.reloadStaticData()
                it.setVolume(0.85f)
                it.play()
            } ?: run {
                audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.7f)
            }
        } catch (_: Exception) {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.7f)
        }
    }

    fun triggerHaptic(hapticEnabled: Boolean, isDelete: Boolean = false) {
        if (!hapticEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = if (isDelete) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                } else {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                }
                vibrator?.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val duration = if (isDelete) 22L else 14L
                val amplitude = if (isDelete) 180 else 120
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (isDelete) 20L else 12L)
            }
        } catch (_: Exception) {
            // Ignore if vibration permission or hardware unavailable
        }
    }

    fun release() {
        try {
            clickAudioTrack?.release()
            clickAudioTrack = null
        } catch (_: Exception) {}
    }
}
