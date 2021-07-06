package com.epnfis.mediaplayertouch

import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer?=null
    private var observer: MediaObserver? = null
    private lateinit var pushButton:Button
    private lateinit var progressBar:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushButton = findViewById(R.id.pushButton)
        progressBar = findViewById(R.id.progressBar)
        mediaPlayer = MediaPlayer.create(this,R.raw.bensound_tenderness)
        mediaPlayer?.setOnPreparedListener {

        }
        mediaPlayer?.setOnCompletionListener {
            if (it != null){
                //progressBar.setProgress(it.getCurrentPosition());

                //println("mediaPlayer?.stop()")
                //mediaPlayer?.stop()
                //println("mediaPlayer?.reset()")
                //mediaPlayer?.reset()
                mediaPlayer?.seekTo(0)
                mediaPlayer?.pause()
            }
            //println(" mediaPlayer = null")
            //mediaPlayer = null
            println(" observer?.stop() ")
            observer?.stop();
            Toast.makeText(applicationContext,"Song play finished",Toast.LENGTH_LONG).show()
            println("Song play finished")
        }
        mediaPlayer?.setOnBufferingUpdateListener { mp, percent ->
            progressBar.secondaryProgress = percent
            println("setOnBufferingUpdateListener $percent")
        }
        pushButton.setOnTouchListener { v, event ->
            handleTouch(event)
            true
        }
    }
    private fun handleTouch(event:MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                println("ACTION_DOWN")
                if (mediaPlayer != null && !mediaPlayer!!.isPlaying){
                    mediaPlayer?.start()
                }
                pushButton.text = "Playing..."
                //mediaPlayer?.seekTo(120000)
                observer = MediaObserver()
                Thread(observer).start()

            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                println("ACTION_CANCEL or ACTION_UP")
                if (mediaPlayer !=null) {
                    //mediaPlayer?.seekTo(0)
                    mediaPlayer?.pause()
                }
                observer?.stop();
                pushButton.text = "Press to Play..."
            }
            else -> {
                println("No Action identified")
            }
        }
    }
    private inner class MediaObserver : Runnable {
        private val stop: AtomicBoolean = AtomicBoolean(false)
        fun stop() {
            stop.set(true)
        }
        override fun run() {
            while (!stop.get()) {
                if(mediaPlayer != null) {
                    val currentPosition = mediaPlayer!!.currentPosition.toDouble()
                    val duration = mediaPlayer!!.duration.toDouble()
                    val progress = currentPosition / duration * 100
                    //println("$currentPosition - $duration - $progress")
                    progressBar.progress = progress.toInt()
                    try {
                        Thread.sleep(200)
                    } catch (ex: Exception) {
                        println(ex.toString())
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mediaPlayer != null){
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
        mediaPlayer=null
    }
}