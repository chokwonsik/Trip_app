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
    fun addOrUpdateTrip(TripData:TripData, title: String, content: String) {
        realm.executeTransaction {
            //DB를 업데이트 하는 쿼리는 반드시 executeTransaction()함수로 감싸야 한다.
            // 동시에 여러곳에서 DB를 수정할 수 없도록 대기 시켜주기 때문에 데이터를 안전하게 업데이트 가능
            TripData.title = title
            TripData.content = content
            TripData.createdAt = Date()

            if(content.length > 100)
                TripData.summary = content.substring(0..100)
            else
                TripData.summary = content

            // Managed 상태가 아닌 경우 copyToRealm() 함수로 DB에 추가
            if(!TripData.isManaged) {
                it.copyToRealm(TripData)
            }
        }
    }

}