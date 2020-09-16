package com.lattice.audioapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lattice.audioapp.R;
import com.lattice.audioapp.adapters.RVAdapter;
import com.lattice.audioapp.models.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Downloaded extends Fragment {


    @BindView(R.id.download_recycler_view)
    RecyclerView downloadRecyclerView;

    RVAdapter rvAdapter;
    List<Song> songList;
    LinearLayoutManager linearLayoutManager;
    Feed.createDataParse createDataParse;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (Feed.createDataParse) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloaded, container, false);
        ButterKnife.bind(this,view);

        songList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext());
        downloadRecyclerView.setLayoutManager(linearLayoutManager);
        rvAdapter = new RVAdapter(songList,getContext());
        downloadRecyclerView.setAdapter(rvAdapter);

        String path = Environment.getExternalStorageDirectory().toString() + "/audioapp";
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        if(file!=null) {
            for (int i = 0; i < file.length; i++) {
                //here populate your listview
                songList.add(new Song(file[i].getName(), file[i].getName(), file[i].getPath()));
                Log.d("Files", "FileName:" + file[i].getName());
            }
        }
        rvAdapter.notifyDataSetChanged();

        rvAdapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song, int position) {
                createDataParse.onDataPass(songList.get(position).getTitle(), songList.get(position).getSource());
                createDataParse.fullSongList(songList, position);
            }
        });

        return view;
    }

    public interface createDataParse {
        public void onDataPass(String name, String path);

        public void fullSongList(List<Song> songList, int position);

        public void currentSong(Song songsList);
        public void getLength(int length);
    }
}
