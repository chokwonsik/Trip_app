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
}
