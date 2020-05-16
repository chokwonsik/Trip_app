package com.kwonsik.chokwonsik.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel() {
    private val memos: MutableList<MemoData> = mutableListOf()
    //MemoData 의 MutableList를 저장하는 속성

    val memoLiveData: MutableLiveData<MutableList<MemoData>> by lazy {
        MutableLiveData<MutableList<MemoData>>().apply {
            value = memos
        }
    }
    // MutableList를 담을 MutableLiveData를 추가
    // 성능을 위해서 lazy를 사용하여 지연 초기화
    // 데이터베이스나 리스트등 ui에서 대량의 데이터를 다루는 경우에는 lazy 내에서 초기값을 할당해주는것이 좋

   fun addMemo(data: MemoData) {
        val tempList = memoLiveData.value //Observer는 값이 변동해야 작동하기 때문에 기존데이터를 가져와서 Observer를 실행
        tempList?.add(data)
        memoLiveData.value = tempList
    }
    // 메모를 리스트에 추가하고 MutableLiveData의 value를 갱신하여 Observer를 호출하도록 하는 함
}