package com.kwonsik.chokwonsik

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kwonsik.chokwonsik.data.ListViewModel
import com.kwonsik.chokwonsik.data.TripListAdapter
import kotlinx.android.synthetic.main.fragment_trip_list.*

class TripListFragment : Fragment() {

    private lateinit var listAdapter: TripListAdapter
    private var viewModel: ListViewModel? = null
    //TripListAdapter와 ListViewModel을 담을 속성을 선언

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.application!!.let {
            ViewModelProvider(
                activity!!.viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it)
            )
                .get(ListViewModel::class.java)
        }

        //TripLiveData를 가져와서 Adapter에 담아 RecyclerView에 출력하도록 함
        viewModel!!.let {
            it.tripLiveData.value?.let {
                listAdapter = TripListAdapter(it)
                tripListView.layoutManager =
                    LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                tripListView.adapter = listAdapter
                listAdapter.itemClickListener = {
                    val intent = Intent(activity, DetailActivity::class.java)
                    intent.putExtra("TRIP_ID", it)
                    startActivity(intent)
                }
            }

            // TripLiveData에 observe 함수를 통해 값이 변할 때 동작할 Observer를 붙여줌
            // Observer 내에서는 adapterd의 갱신 코드를 호출
            it.tripLiveData.observe(this,
                Observer {
                    listAdapter.notifyDataSetChanged()
                }
            )
        }

    }
    // Trip를 작성하고 되돌아 왔을 때 리스트가 갱신되도록 함.
    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }
}
