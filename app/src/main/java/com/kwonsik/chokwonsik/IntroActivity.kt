package com.kwonsik.chokwonsik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

class IntroActivity : AppCompatActivity() {

    var handler: Handler? = null // Handler : Runnable 을 실행하는 클래스
    var runnable: Runnable? = null // Runnable : 병렬 실행이 가능한 Thread를 만들어주는 클래스


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // 안드로이드 앱을 띄우는 window의 속성을 변경하여 시스템 ui(작업표시)를 숨기고 전체화면으로 표시하는 기

    }

    override fun onResume() { // Runnable이 실행되면 ListActivity로 이동하는 코드

        super.onResume()

        runnable = Runnable {
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
        }

        // Handler를 생성하고 2000ms runnable을 실
        handler = Handler()
        handler?.run {
            postDelayed(runnable, 4000)
        }
    }

    override fun onPause() { //Activity Pause 상태일 때는 runnable도 중단하도록 함.
        super.onPause()

        handler?.removeCallbacks(runnable)
    }
}
