package com.kwonsik.chokwonsik.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class DetailViewModel : ViewModel() {

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

    // 날씨정보 삭제
    fun deleteWeather() {
        tripData.weather = ""
        tripLiveData.value = tripData
    }

    // 받아 온 좌표를 WeatherData에 넘겨 날씨를 가져와 tripData에 저장하는 함
    fun setWeather(latitude: Double, longitude: Double) {
        // viewmodelScope는 ViewModel 이 소멸할 때에 맞춰 코루틴을 정지시켜줌
        viewModelScope.launch {
            tripData.weather = WeatherData.getCurrentWeather(latitude, longitude)
            tripLiveData.value = tripData
        }
    }

    fun setImageFile(context: Context, bitmap: Bitmap) {
        val imageFile = File(
            // context의 getDir()함수를 통해 앱 데이터 폴더 내에 image라는 하위 폴더 지정
            context.getDir("image", Context.MODE_PRIVATE),
            tripData.id + ".jpg")

        // 기존에 파일이 있는지 체크하여 삭제함.
        if (imageFile.exists()) imageFile.delete()

        try {
            imageFile.createNewFile()
            val outputStream = FileOutputStream(imageFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()

            tripData.imageFile = tripData.id + ".jpg"
            tripLiveData.value = tripData
        }
        catch (e: Exception) {
            println(e)
        }
    }

    fun addOrUpdateTrip(context: Context) {
        tripDao.addOrUpdateTrip(tripData)
    }
}