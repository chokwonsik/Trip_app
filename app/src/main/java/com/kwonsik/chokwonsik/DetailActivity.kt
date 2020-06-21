package com.kwonsik.chokwonsik

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Px
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.kwonsik.chokwonsik.data.DetailViewModel

import com.google.android.material.snackbar.Snackbar

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.MapView
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons

import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.view_info.view.*
import java.io.File
import java.lang.NullPointerException
import java.util.*


class DetailActivity : AppCompatActivity() {

    private var viewModel: DetailViewModel? = null

    private val REQUEST_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(DetailViewModel::class.java)
        }

        // tripLivaData observe
        viewModel!!.tripLiveData.observe(this, Observer {
            supportActionBar?.title = it.title
            contentEdit.setText(it.content)
            locationInfoView.setLocation(it.latitude, it.longitude)
            weatherInfoView.setWeather(it.weather)

            // 이미지 파일 경로를 Uri로 바꾸어 bglmage에 설정함.
            val imageFile = File(
                getDir("image", Context.MODE_PRIVATE),
                it.imageFile)

            bgImage.setImageURI(imageFile.toUri())
        })

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

                    // 제목이 변경될 때 viewModel의 tripData도 함께 갱신시킴
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

            // viewModel 에 저장되어 있는 좌표가 유효한지부터 확인함.
            val latitude = viewModel!!.tripData.latitude
            val longitude = viewModel!!.tripData.longitude

            if (!(latitude == 0.0 && longitude == 0.0)) {
                // 네이버 지도 sdk 에서 제공하는 MapView 객체 생성 (지도를 출력하는 View)
                val mapView = MapView(this)
                // 네이버 지도는 맵이 로드된 후에 NaverMap 객체를 받은 후에만 옵션의 변경이 가능

                // NaverMap 객체를 받기 위해서 getMapAsync() 함수에 리스너를 설정
                mapView.getMapAsync {
                    // 여행의 위치정보를 NaverMap의 cameraUpdate 객체에 넣어줌
                    val latitude = viewModel!!.tripData.latitude
                    val longitude = viewModel!!.tripData.longitude
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
                    // it 변수에 반환된 현재 지도의 NaverMap 객체에 카메라 위치를 적용
                    it.moveCamera(cameraUpdate)

                    it.setOnMapLongClickListener { point, coord ->
                        Toast.makeText(
                            this, "${coord.latitude}, ${coord.longitude}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    val uiSettings = it.uiSettings
                    uiSettings.isCompassEnabled = true
                    uiSettings.isIndoorLevelPickerEnabled = true
                    uiSettings.isLocationButtonEnabled = true

                    fun marker_list(latitude: Double, longitude: Double, name_place: String)
                    {
                        val marker = Marker()
                        marker.position = LatLng(latitude, longitude)
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_location2)
                        marker.icon = MarkerIcons.BLACK
                        marker.iconTintColor = Color.BLUE
                        marker.width = 50
                        marker.height = 80
                        marker.isIconPerspectiveEnabled = true
                        marker.captionText = name_place
                        marker.map = it
                        marker.isHideCollidedSymbols = true
                    }

                    marker_list(latitude,longitude,"현재위치")
                    marker_list(37.567150, 126.978046,"서울시청")
                    marker_list(33.484365, 126.463622,"돈사돈")
                    marker_list(33.309276, 126.633837,"휴애리 자연 생활공")
                    marker_list(33.306081, 126.289459,"오설록 티 뮤지엄")
                    marker_list(33.528692, 126.771460,"만장굴")
                    marker_list(33.248163, 126.554417,"천지연 폭포")
                    marker_list(33.394131, 126.239687,"협재 해수욕장")
                    marker_list(33.250498, 126.412194,"테디베어뮤지엄")
                    marker_list(33.511805, 126.526120,"동문재래시")

                }

                // 모든 설정이 끝난 mapView는 AlertDialog에 설정하여 출력
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    // IntroActivity에서 이미 체크한 위치 권한 허용 여부를 다시 체크하지 않기 위해서 함수에
    // annotation을 추가함
    @SuppressLint("MissingPermission")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_location -> {
                // AlertDialog.Builder() 를 사용해서 위치정보 설정을 묻는 다이얼로그를 추가
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("현재 위치를 저장하거나 삭제할 수 있습니다.")
                    .setPositiveButton("위치지정", DialogInterface.OnClickListener { dialog, which ->

                        // locationManager를 가져와서 위치기능이 켜져있는지 확인 (gps 및 네트워크 기능을 둘다 확인해야함)
                        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                        // 위치 기능이 둘 다 꺼진경우 SnackBar 를 띄워 시스템의 위치 옵션화면을 안내해 줌
                        if (!isGPSEnabled && !isNetworkEnabled) {
                            Snackbar.make(
                                toolbarLayout,
                                "폰의 위치기능을 켜야 기능을 사용할 수 있습니다.",
                                Snackbar.LENGTH_LONG)
                                .setAction("설정", View.OnClickListener {
                                    val goToSettings = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivity(goToSettings)
                                }).show()
                        }
                        // 위치기능이 하나라도 켜져있을 때의 분기 코드
                        else {
                            // Criteria 객체에 위치 정확도와 배터리 소모량을 설정함
                            val criteria = Criteria()
                            criteria.accuracy = Criteria.ACCURACY_MEDIUM
                            criteria.powerRequirement = Criteria.POWER_MEDIUM

                            // locationManager 의 requestSingleUpdate() 함수를 이용하여 위치정보를 1회 받아오는 코드
                            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                                    // 실제 구현할 함수는 위치정보가 갱신될 때 샐행되는 onLocationChanged()로 위치값을 받아 ViewModel에 넘겨주면 됨
                                    override fun onLocationChanged(location: Location?) {
                                        location?.run {
                                            viewModel!!.setLocation(latitude, longitude)
                                        }
                                    }

                                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle? ) {
                                    }

                                    override fun onProviderEnabled(provider: String?) {
                                    }

                                    override fun onProviderDisabled(provider: String?) {
                                    }

                                },null)
                        }
                    })
                    .setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        viewModel!!.setLocation(0.0, 0.0)
                    })
                    .show()
            }

            R.id.menu_weather -> {
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("현재 날씨를 저장하거나 삭제할 수 있습니다.")
                    .setPositiveButton("날씨 가져오기", DialogInterface.OnClickListener { dialog, which ->
                        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                        if (!isGPSEnabled && !isNetworkEnabled) {
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

                            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                                    override fun onLocationChanged(location: Location?) {
                                        location?.run {
                                            //위치 기능이 켜져있을 받아온 위치를 setWeather() 함수에 전달함
                                            viewModel!!.setWeather(latitude, longitude)
                                        }
                                    }

                                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                                    }

                                    override fun onProviderEnabled(provider: String?) {
                                    }

                                    override fun onProviderDisabled(provider: String?) {
                                    }

                                },null)
                        }
                    })
                    .setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        viewModel!!.deleteWeather()
                    })
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 요청했던 코드가 동일한지 결과값이 OK 인지 확인함.
        if(requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                // 결과값으로 들어온 데이터를 비트맵으로 변화함.
                val inputStream = data?.data?.let { contentResolver.openInputStream(it) }
                inputStream?.let {
                    val image = BitmapFactory.decodeStream(it)

                    // bgImage 에 표시되는 이미지를 null로 초기화 하고 새 이미지를 viewModel 에 설정함.
                    bgImage.setImageURI(null)
                    image?.let { viewModel?.setImageFile(this, it) }

                    // 작업이 끝나면 inputStream 은 닫아줌.
                    it.close()
                }
            }
            catch (e: Exception) {
                println(e)
            }
        }
    }
}
