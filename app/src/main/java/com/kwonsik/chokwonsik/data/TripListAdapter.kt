package com.kwonsik.chokwonsik.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kwonsik.chokwonsik.R
import kotlinx.android.synthetic.main.item_trip.view.*
import java.text.SimpleDateFormat

class TripListAdapter(private val list: MutableList<TripData>):
    RecyclerView.Adapter<ItemViewHolder>() {

    private val dateFormat = SimpleDateFormat("MM/dd HH:mm")
    // Date 객체를 사람이 볼 수있는 문자열로 변환하기 위한 객체
    // "MM//dd HH:MM" : 월/일 시:분 으로 출력

    lateinit var itemClickListener: (itemId: String) -> Unit

    // item_Trip를 불러 ViewHolder를 생성함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        view.setOnClickListener { //아이템이 클릭될 때 view의 tagdptj Trip id를 받아서 리스너에 넘김
            itemClickListener?.run {
                val tripId = it.tag as String
                this(tripId)
            }
        }
        return ItemViewHolder(view)
    }

    // list내의 TripData의 개수를 반환
    override fun getItemCount(): Int {
        return list.count()
    }

    //제목이 있는 경우 titleView를 화면에 표시(VISIBLE)하고 title 값을 할당하여 보여 줌
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (list[position].title.isNotEmpty()) {
            holder.containerView.titleView.visibility = View.VISIBLE
            holder.containerView.titleView.text = list[position].title
        }

        else {         // 제목이 없는 경우 titleView의 영역까지 숨겨줌(GONE)

            holder.containerView.titleView.visibility = View.GONE
        }
        // View의 visibility 종류
        // VISIBLE: View를 화면에 표시
        // INVISIBLE: View의 내용만 감추고 영역은 유지
        // GONE: View의 내용 및 영역까지 제거

        holder.containerView.summaryView.text = list[position].summary
        // 요약내용(summary)을 summaryView에 표시
        holder.containerView.dateView.text = dateFormat.format(list[position].createdAt)
        // 작성시간(createAt)을 dataFormat으로 변환하여 dataView에 표시함
        holder.containerView.tag = list[position].id
    }
}