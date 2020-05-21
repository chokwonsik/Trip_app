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
        tripData = realm.copyFromRealm(tripDao.selectTrip(id))
        tripLiveData.value = tripData
    }

    fun deleteLocation() {
        tripData.latitude = 0.0
        tripData.longitude = 0.0
        tripLiveData.value = tripData
    }

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