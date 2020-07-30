package com.kwonsik.chokwonsik.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

class WeatherData {

    companion object {

        //xml을 파싱하기위한 XmlPullParser를 생성하는 XmlPullParserFactory의 객체를 생성
        private val xmlPullParserFactory by lazy { XmlPullParserFactory.newInstance() }

        // suspend를 붙여 코루틴에서 언제든지 중지 또는 재개할 수 있도록 한다.
        suspend fun getCurrentWeather(latitude: Double, longitude: Double): String {
            // 네트워크에서 처리되므로 코루틴을 사용하여 IO 쓰레드에서 동작 ( Exception 발생 예방)
            return GlobalScope.async(Dispatchers.IO) {
                val requestUrl = "https://api.openweathermap.org/data/2.5/weather" +
                        "?lat=${latitude}&lon=${longitude}&mode=xml&units=metric&" +
                        "&appid=api"

                // 결과값인 날씨(문자열)를 저장하는 변수
                var currentWeather = ""

                try {
                    // 요청할 주소 문자열을 URL 객체로 만들어 줌.
                    val url = URL(requestUrl)

                    // URl에 데이터를 요청하고 결과를 가져오는 입력 stream을 열어 줌.
                    val stream = url.openStream()

                    // 결과를 파싱할 수 있는 XmlPullParser를 Factory 에서 가져옴.
                    val parser = xmlPullParserFactory.newPullParser()

                    // URL의 입력스트림을 UTF-8 인코딩 방식으로 읽을 수 있는 InputStreamReader 객체에 넣어 parser 에 입력해 줌
                    parser.setInput(InputStreamReader(stream, "UTF-8"))

                    // --------요청된 날씨의 결과를 parse에서 처리할 수 있는 준비 완료 상태 ---------

                    // eventType 은 문서의 시작과 끝, 태그의 시작과 끝, 태그내의 텍스트 등을 분류해서 알려줌
                    // XmlPullParser 는 문서의 시작부터 순서대로 이벤트를 파싱
                    var eventType = parser.eventType
                    // 결과 xml에 담긴 날씨 코드를 담을 변
                    var currentWeatherCode = 0

                    // XmlPullParser 를 이용하여 문서가 끝날때까지 반복.
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        // weather 태그 시작점 찾기.
                        if (eventType == XmlPullParser.START_TAG && parser.name == "weather") {
                            // weather 태그의 number 속성을 Int 타입으로 반환하여 가져온 후 반복문을 중지.
                            currentWeatherCode =
                                parser.getAttributeValue(null, "number").toInt()
                            break
                        }
                        // 반복할 때마다 next() 함수를 호출하여 다음 event 를 파싱하고 eventType 을 반환받음.
                        eventType = parser.next()
                    }

                    Log.d(" 날씨 : ",currentWeatherCode.toString())

                    when (currentWeatherCode) {
                        in 200..299 -> currentWeather = "뇌우"
                        in 300..399 -> currentWeather = "이슬비"
                        in 500..599 -> currentWeather = "비"
                        in 600..699 -> currentWeather = "눈"
                        in 700..761 -> currentWeather = "안개"
                        771 -> currentWeather = "돌풍"
                        781 -> currentWeather = "토네이도"
                        800 -> currentWeather = "맑음"
                        in 801..802 -> currentWeather = "구름조금"
                        in 803..804 -> currentWeather = "구름많음"
                        else -> currentWeather = ""
                    }

                    // 네트워크를 통해 날씨 정보를 쿼리하므로 Exception 처리가 필요함
                } catch (e: Exception) {
                    println(e)
                }

                currentWeather
            }.await() //코루틴의 결과는 await()함수로 기다린 후 반홤함.
        }
    }
}
