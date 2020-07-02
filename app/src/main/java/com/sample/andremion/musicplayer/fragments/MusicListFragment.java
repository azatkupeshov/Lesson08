package com.sample.andremion.musicplayer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.databases.DatabaseHelper;
import com.sample.andremion.musicplayer.music.MusicContent;
import com.sample.andremion.musicplayer.music.MusicFinder;
import com.sample.andremion.musicplayer.view.ItemTouchHelper.ItemTouchHelperCallback;
import com.sample.andremion.musicplayer.view.ItemTouchHelper.OnStartDragListener;
import com.sample.andremion.musicplayer.view.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class MusicListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private MusicContent content;
    private RecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<MusicContent.MusicItem> musicItems;
    private ItemTouchHelper itemTouchHelper;

    public MusicListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        //Инициализация RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.tracks);
        assert recyclerView != null;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        //Данные для заполнения RecyclerView
        content = new DatabaseHelper(getContext());
        //Инициализация адаптера и заполнение данных
        adapter = new RecyclerViewAdapter(content.getAllSongs(), getContext());
        recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    public void onRefresh() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (content != null && adapter != null) {
            if (musicItems != null) {
                musicItems.clear();
                musicItems.addAll(content.getAllSongs());
            }
            adapter.notifyDataSetChanged();
        }
    }
}
