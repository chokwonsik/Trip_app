package com.kwonsik.chokwonsik

import android.content.Context
import android.location.Geocoder
import android.util.AttributeSet
import com.kwonsik.chokwonsik.data.WeatherData
import kotlinx.android.synthetic.main.view_info.view.*
import java.util.*


    class WeatherInfoView @JvmOverloads constructor (context: Context,
                                                     attrs: AttributeSet? = null,
                                                     defStyleAttr: Int = 0)
        : InfoView(context, attrs, defStyleAttr) {

        // View에서 표시할 이미지를 ic_weather으로 바꾸고 텍스트는 비워 줌.
        init {
        typeImage.setImageResource(R.drawable.ic_weather)
        infoText.setText("")
    }

    // 날씨 값을 입력받았을 때 표시하기 위한 함수
    fun setWeather(weatherText: String) {
        if(weatherText.isEmpty()) {
            infoText.setText("날씨정보가 없습니다")
        }
        else {
            infoText.setText(weatherText)
        }
    }
}