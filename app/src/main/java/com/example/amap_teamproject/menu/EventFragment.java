package com.example.amap_teamproject.menu;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.amap_teamproject.R;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {

    private static final String ARG_EVENTS = "events";
    private List<Event> eventList;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance(String title, List<Event> events) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_EVENTS, new ArrayList<>(events));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventList = getArguments().getParcelableArrayList(ARG_EVENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new EventAdapter(eventList));
        return view;
    }
}
