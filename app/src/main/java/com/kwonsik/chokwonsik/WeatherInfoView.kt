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

    init {
        typeImage.setImageResource(R.drawable.ic_weather)
        infoText.setText("")
    }

    fun setWeather(weatherText: String) {
        if(weatherText.isEmpty()) {
            infoText.setText("날씨정보가 없습니다")
        }
        else {
            infoText.setText(weatherText)
        }
    }
}