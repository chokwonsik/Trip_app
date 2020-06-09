package com.kwonsik.chokwonsik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kwonsik.chokwonsik.data.TripListAdapter
import com.kwonsik.chokwonsik.data.ListViewModel


import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    private lateinit var listAdapter: TripListAdapter
    private var viewModel: ListViewModel? = null
    // ListViewModel을 담을 변수를 추
    // TripListAdapter와 ListViewModel을 담을 속성을 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        val fragmentTransation = supportFragmentManager.beginTransaction()
        fragmentTransation.replace(R.id.contentLayout, TripListFragment())
        fragmentTransation.commit()
        // TripListFragment를 화면에 표시

        // ListViewModel을 가져오는 코드
        viewModel = application!!.let {
            // 앱의 객체인 application이 null인지 먼저 체크

            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                // ViewModel을 가져오기 위해 ViewModelProvider 객체를 생성
                // 액티비티의 속성인 viewModelStore, AndroidViewModelFactory
                // viewModelStore 생성과 소멸의 기준
                // ViewModelFactory는 ViewModeld 을 실제로 생성하는 객체

                .get(ListViewModel::class.java)
        }       //ViewModelProvider의 get 함수를 통해 ListViewModel을 얻을 수 있음

        // fab 감지시 DetailActivity로 이동
        fab.setOnClickListener { view ->
            val intent = Intent(applicationContext, DetailActivity::class.java)
            startActivity(intent)
            }
            // Dummy 테스트를 위한 TripData를 생성하여 ViewModel addTrip에 전달하여 추가
        }

    }


