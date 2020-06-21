package com.kwonsik.chokwonsik

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import io.realm.Realm

//앱을 제어하는 객체인 Application을 상속받아 KwonsikTripApplication 클래스를생
class KwonsikTripApplication() : Application() {
    // 앱 시작시 실행되는 onCreate()함수를 override 하고
    // 그 안에서 Realm 데이터베이스를 초기화 함
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        NaverMapSdk.getInstance(this).setClient(
            NaverMapSdk.NaverCloudPlatformClient("jsojqwdhkb"))

    }

}