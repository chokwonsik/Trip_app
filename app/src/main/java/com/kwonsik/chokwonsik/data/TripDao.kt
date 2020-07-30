package com.kwonsik.chokwonsik.data

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

class TripDao(private val realm: Realm) {

    // DB에 담긴 TripData를 생성시간의 역순으로 정렬하여 받아
    fun getAllTrips(): RealmResults<TripData> {
        return realm.where(TripData::class.java)
            .sort("createdAt", Sort.DESCENDING)
            .findAll()
    }

    //지정된 id의 Trip를 가져와서 반환하는 함수
    fun selectTrip(id: String): TripData {
        return realm.where(TripData::class.java)
            .equalTo("id", id)
            .findFirst() as TripData
    }

    // Trip를 생성하거나 수정하는 함수
    fun addOrUpdateTrip(tripData:TripData) {
        realm.executeTransaction {
            tripData.createdAt = Date()

            if(tripData.content.length > 100)
                tripData.summary = tripData.content.substring(0..100)
            else
                tripData.summary = tripData.content

            it.copyToRealmOrUpdate(tripData)
        }
    }
}

