package com.example.amap_teamproject.SearchPage;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import java.util.List;

public class SR_EventFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;

    public SR_EventFragment() {
        // Required empty public constructor
    }

    public SR_EventFragment(List<Event> eventList, EventAdapter eventAdapter) {
        this.eventList = eventList;
        this.adapter = eventAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_event);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (adapter == null) {
            adapter = new EventAdapter(eventList);
        }
        recyclerView.setAdapter(adapter);

        return view;
    }
}

