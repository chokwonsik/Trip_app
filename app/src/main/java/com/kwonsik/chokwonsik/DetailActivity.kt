package com.kwonsik.chokwonsik

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kwonsik.chokwonsik.R
import com.kwonsik.chokwonsik.data.DetailViewModel
import com.naver.maps.map.MapView
import com.
import com.kwonsik.chokwonsik.data.ListViewModel
import com.kwonsik.chokwonsik.data.TripData
import com.kwonsik.chokwonsik.data.TripListAdapter
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.dialog_title.*
import kotlinx.android.synthetic.main.fragment_trip_list.*
import java.util.*

class DetailActivity : AppCompatActivity() {

    private var viewModel: DetailViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(DetailViewModel::class.java)
        }

        viewModel!!.tripLiveData.observe(this, Observer {
            supportActionBar?.title = it.title
            contentEdit.setText(it.content)
            locationInfoView.setLocation(it.latitude, it.longitude)
        })

//        // 제목과 내용에 Observer를 걸어 화면을 갱신
//        viewModel!!.let {
//            it.title.observe(this, Observer { supportActionBar?.title = it } )
//            it.content.observe(this, Observer { contentEdit.setText(it) } )
//        }

        // ListActivity에서 아이템을 선택했을때 보내주는 Trip id로 데이터를 로드함.
        val tripId = intent.getStringExtra("TRIP_ID")
        if (tripId != null) viewModel!!.loadTrip(tripId)

        // 툴바 레이아웃을 눌럿을 때 제목을 수정하는 다이얼로그를 띄우는 루틴
        toolbarLayout.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null)
            val titleEdit = view.findViewById<EditText>(R.id.titleEdit)

            AlertDialog.Builder(this)
                .setTitle("제목을 입력하세요")
                .setView(view)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    supportActionBar?.title = titleEdit.text.toString()
                    toolbarLayout.title = titleEdit.text.toString()
                    viewModel!!.tripData.title = titleEdit.text.toString()
                }).show()
        }


        // 내용이 변경될 때마다 Listener 내에서 viewModel의 tripData의 내용도 같이 변경해
        contentEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel!!.tripData.content = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        locationInfoView.setOnClickListener {
            val latitude = viewModel!!.tripData.latitude
            val longitude = viewModel!!.tripData.longitude

            if (!(latitude == 0.0 && longitude == 0.0)) {
                val mapView = MapView(this)
                mapView.getMapAsync {
                    val latitude = viewModel!!.tripData.latitude
                    val longitude = viewModel!!.tripData.longitude
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
                    it.moveCamera(cameraUpdate)
                }
                AlertDialog.Builder(this)
                    .setView(mapView)
                    .show()
            }
        }
    }

    // 뒤로가기를 눌렀을 때 동작하는 onBackPressed()를 override 함
    // 이 화면에서 뒤로가기를 누를 때 viewModel addOrUpdateTrip()를 호출하여 Trip를 DB에 갱신
    override fun onBackPressed() {
        super.onBackPressed()
        viewModel?.addOrUpdateTrip(this)

//            viewModel?.addOrUpdateTrip(
//                supportActionBar?.title.toString(),
//                contentEdit.text.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    // IntroActivity에서 이미 체크한 위치 권한 허용 여부를 다시 체크하지 않기 위해서 함수에
    // annotation을 추가함
    @SuppressLint("MissingPermission")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_location -> {
                // AlertDialog.Builder() 를 사용해서 위치정보 설정을 묻는 다이얼로그를 추가
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("현재 위치를 메모에 저장하거나 삭제할 수 있습니다.")
                    .setPositiveButton("위치지정", DialogInterface.OnClickListener { dialog, which ->

                        // locationManager를 가져와서 위치기능이 켜져있는지 확인 (gps 및 네트워크 기능을 둘다 확인해야함)
                        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                        // 위치 기능이 둘 다 꺼진경우 SnackBar 를 띄워 시스템의 위치 옵션화면을 안내해 줌
                        if(!isGPSEnabled && !isNetworkEnabled) {
                            Snackbar.make(
                                toolbarLayout,
                                "폰의 위치기능을 켜야 기능을 사용할 수 있습니다.",
                                Snackbar.LENGTH_LONG)
                                .setAction("설정", View.OnClickListener {
                                    val goToSettings = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivity(goToSettings)
                                }).show()
                        }
                        else {
                            val criteria = Criteria()
                            criteria.accuracy = Criteria.ACCURACY_MEDIUM
                            criteria.powerRequirement = Criteria.POWER_MEDIUM

                            locationManager.requestSingleUpdate(criteria, object :
                                LocationListener {
                                override fun onLocationChanged(location: Location?) {
                                    location?.run {
                                        viewModel!!.setLocation(latitude, longitude)
                                    }
                                }

                                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                                }

                                override fun onProviderEnabled(provider: String?) {
                                }

                                override fun onProviderDisabled(provider: String?) {
                                }

                            }, null)
                        }
                    })
                    .setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        viewModel!!.setLocation(0.0, 0.0)
                    })
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
