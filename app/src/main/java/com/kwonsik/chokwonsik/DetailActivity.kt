package com.kwonsik.chokwonsik

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
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
import com.kwonsik.chokwonsik.data.ListViewModel
import com.kwonsik.chokwonsik.data.TripData
import com.kwonsik.chokwonsik.data.TripListAdapter
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.dialog_title.*
import kotlinx.android.synthetic.main.fragment_trip_list.*

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

        // 제목과 내용에 Observer를 걸어 화면을 갱신
        viewModel!!.let {
            it.title.observe(this, Observer { supportActionBar?.title = it } )
            it.content.observe(this, Observer { contentEdit.setText(it) } )
        }

        // ListActivity에서 아이템을 선택했을때 보내주는 Trip id로 데이터를 로드함.
        val memoId = intent.getStringExtra("MEMO_ID")
        if(memoId != null) viewModel!!.loadTrip(memoId)

        // 툴바 레이아웃을 눌럿을 때 제목을 수정하는 다이얼로그를 띄우는 루틴
        toolbarLayout.setOnClickListener {

            // Layoutlnflater로 레이아웃 xml을 view로 변환
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null)

            // View의 findViewByld 함수를 이용하여 view에 포함된 titleEdit을 변수에 담음
            val titleEdit = view.findViewById<EditText>(R.id.titleEdit)

            AlertDialog.Builder(this)
                .setTitle("제목을 입력하세요")
                .setView(view)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    supportActionBar?.title = titleEdit.text.toString()
                    toolbarLayout.title = titleEdit.text.toString()
                }).show()
        }
    }

    // 뒤로가기를 눌렀을 때 동작하는 onBackPressed()를 override 함
    // 이 화면에서 뒤로가기를 누를 때 viewModel addOrUpdateTrip()를 호출하여 Trip를 DB에 갱신
    override fun onBackPressed() {
        super.onBackPressed()

        viewModel?.addOrUpdateTrip(
            supportActionBar?.title.toString(),
            contentEdit.text.toString()
        )
    }
}
