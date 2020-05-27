package com.kwonsik.chokwonsik.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import java.util.*

class DetailViewModel : ViewModel() {
//    val title: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
//    val content: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
//
//    private var tripData = TripData()

    // 새로운 tripData 변수 및 tripLivaData 변수 추가
    var tripData = TripData()
    val tripLiveData: MutableLiveData<TripData> by lazy {
        MutableLiveData<TripData>().apply { value = tripData }
    }

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val tripDao: TripDao by lazy {
        TripDao(realm)
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun loadTrip(id: String) {
        // copyFromRealmd() 으로 unmanaged 객체로 만들어 직접 사용
        tripData = realm.copyFromRealm(tripDao.selectTrip(id))
        // unmanaged TripData 객체를 tripLiveData에 할당
        tripLiveData.value = tripData
    }

    // 위치정보 삭제
    fun deleteLocation() {
        tripData.latitude = 0.0
        tripData.longitude = 0.0
        tripLiveData.value = tripData
    }

    // 위치정보 설정
    fun setLocation(latitude: Double, longitude: Double) {
        tripData.latitude = latitude
        tripData.longitude = longitude
        tripLiveData.value = tripData
    }

    fun addOrUpdateTrip(context: Context) {
        tripDao.addOrUpdateTrip(tripData)

//        AlarmTool.deleteAlarm(context, tripData.id)
//        if(tripData.alarmTime.after(Date())) {
//            AlarmTool.addAlarm(context, tripData.id, tripData.alarmTime)
//        }
    }


//    // Trip을 수정할 때 사용하기 위해 Trip의 id를 받아 tripData 를 로드하는 함수
//    fun loadTrip(id: String) {
//        tripData = tripDao.selectTrip(id)
//        title.value = tripData.title
//        content.value = tripData.content
//    }
//
//    // Trip의 추가나 수정시 사용하기 위해 TripDao와 연결
//    fun addOrUpdateTrip(title: String, content: String) {
//        tripDao.addOrUpdateTrip(tripData, title, content)
//    }

}