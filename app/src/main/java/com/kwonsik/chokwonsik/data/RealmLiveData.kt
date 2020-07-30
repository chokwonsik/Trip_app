package com.kwonsik.chokwonsik.data

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults

// LiveData를 상속받아 class를 만들고 생성자에서 RealmResults를 받
class RealmLiveData<T: RealmObject> (private val realmResults: RealmResults<T>)
    : LiveData<RealmResults<T>> () {

    //받아온 realm Result를 value에 추가 (Observe가 동잗하도록 하기 위해)
    init {
        value = realmResults
    }

    // RealmResult가 갱신될때 동작할 리스너 (갱신되는 값을 value 에 할당)
    private val listener =
        RealmChangeListener<RealmResults<T>> { value = it }

    // LiveData가 활성화 될 때 realmResults에 리스너를 붙여줌.
    override fun onActive() {
        super.onActive()
        realmResults.addChangeListener(listener)
    }

    // LiveData가 비활성화 될 때 리스너를 제거
    override fun onInactive() {
        super.onInactive()
        realmResults.removeChangeListener(listener)
    }
}