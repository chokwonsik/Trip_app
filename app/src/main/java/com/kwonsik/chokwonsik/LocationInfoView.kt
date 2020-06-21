package com.kwonsik.chokwonsik

import android.content.Context
import android.location.Geocoder
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.res.TypedArrayUtils.getText
import kotlinx.android.synthetic.main.view_info.view.*
import java.util.*
import java.lang.Object



// InfoView와 같은 생성자를 만들고 Infoview를 상속받아 생성자에 패러미터를 넘김
class LocationInfoView @JvmOverloads constructor (context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0)
    : InfoView(context, attrs, defStyleAttr) {

    // init 에서는 View에서 표시할 이미지를 ic_location으로 바꾸고 텍스트는 비워 줌
    init {
        typeImage.setImageResource(R.drawable.ic_location)
        infoText.setText("")
    }


    // 위치 값을 입력받았을때 표기하기 위한 함수
    fun setLocation(latitude: Double, longitude: Double) {

        Log.d(" lati_long","위도 : "+latitude.toString()+", 경도 : "+longitude.toString())

        if(latitude == 0.0 && longitude == 0.0) {
            infoText.setText("위치정보가 없습니다")
        }
        else { // 위치 값이 있다면 구글에서 제공하는 Geocoder로 좌표를 지역 이름으로 변환하여 출력
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            infoText.setText("${addresses[0].adminArea}")
        }
    }
}