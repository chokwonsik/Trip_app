package com.kwonsik.chokwonsik

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class IntroActivity : AppCompatActivity() {

    var handler: Handler? = null // Handler : Runnable 을 실행하는 클래스
    var runnable: Runnable? = null // Runnable : 병렬 실행이 가능한 Thread를 만들어주는 클래스

    // 권한 요청시 권한 Activity에 전달할 고유 코드(상수) 추가
    companion object {
        private const val REQUEST_LOCATION_PERMISSION_CODE = 100
    }

    // 위치 권한이 허용되어 있는지 체크하는 함수 (Manifest에 추가한것)
    fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission && coarseLocationPermission
    }

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

    // Runnable이 실행되면 ListActivity로 이동하는 코드
    fun moveListActivity() {
        runnable = Runnable {
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
            finish()
        }

        handler = Handler()
        handler?.run {
            postDelayed(runnable, 2000)
        }
    }

    override fun onResume() {
        super.onResume()

        // 위치 권한이 이미 주어진 경우 ListActivity 로 바로 이동함
        if (checkLocationPermission()) {
            moveListActivity()
        } else { //shouldShowRequestPermissionRationale 함수로 사용자가 권한을 거절했던 적이 있는지 확인하고 안내 메시지를 출력
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Toast.makeText(this,
                    "이 앱을 실행하려면 위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }

            // 앱에 필요한 권한을 사용자에게 요청하는 시스템 Activity를 띄움
            ActivityCompat.requestPermissions(
                this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_LOCATION_PERMISSION_CODE)
        }
    }

    override fun onPause() {
        super.onPause()

        handler?.removeCallbacks(runnable)
    }
}
