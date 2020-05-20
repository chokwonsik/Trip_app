package com.kwonsik.chokwonsik.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class TripData ( // RealmObject는 반드시 open 이어야
    @PrimaryKey
    // 테이블에서 각각의 레코드를 고분할 수 있는 고유값으로 지정하기 위함 (업데이트나 삭제의 기준값)
    var id: String = UUID.randomUUID().toString(), // id 속성의 초기값인 UUID는 랜덤한 고유값을 자동으로 생성
    var createdAt: Date = Date(), //작성 시간
    var title: String = "", // 제목
    var content: String = "", // 내용
    var summary: String = "", // 내용요약
    var imageFile: String = "", //첨부이미지 파일 이름
    var latitude: Double = 0.0, // 위도
    var longitude: Double = 0.0, // 경도
    var alarmTime: Date = Date(), // 알람시간
    var weather: String = "" // 날씨
): RealmObject() //RealmObject를 상속받도록 함

