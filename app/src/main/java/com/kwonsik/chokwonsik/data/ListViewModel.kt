package com.kwonsik.chokwonsik.data

import androidx.lifecycle.ViewModel
import io.realm.Realm

class ListViewModel : ViewModel() {
    // Realm 인스턴스를 생성하여 사용하는 변수
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    // Realm 인스턴스를 넣어 TripDao를 생성하여 사용하는 변
    private val tripDao: TripDao by lazy {
        TripDao(realm)
    }

    // TripDao에서 모든 Trip를 가져와서 RealmLiveData로 변환하여 사용하는 변수.
    val tripLiveData: RealmLiveData<TripData> by lazy {
        RealmLiveData<TripData> (tripDao.getAllTrips())
    }

    // LiveViewModel을 더이상 사용하지 않을때 Realm 인스턴스를 닫아 줌.
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }


    /*
    private val trips: MutableList<TripData> = mutableListOf()
    //TripData 의 MutableList를 저장하는 속성

    val tripLiveData: MutableLiveData<MutableList<TripData>> by lazy {
        MutableLiveData<MutableList<TripData>>().apply {
            value = trips
        }
    }
    // MutableList를 담을 MutableLiveData를 추가
    // 성능을 위해서 lazy를 사용하여 지연 초기화
    // 데이터베이스나 리스트등 ui에서 대량의 데이터를 다루는 경우에는 lazy 내에서 초기값을 할당해주는것이 좋

   fun addTrip(data: TripData) {
        val tempList = tripLiveData.value //Observer는 값이 변동해야 작동하기 때문에 기존데이터를 가져와서 Observer를 실행
        tempList?.add(data)
        tripLiveData.value = tempList
    }
    // 메모를 리스트에 추가하고 MutableLiveData의 value를 갱신하여 Observer를 호출하도록 하는 함
    */

}
