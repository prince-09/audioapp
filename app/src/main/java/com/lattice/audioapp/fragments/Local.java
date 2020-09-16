package com.lattice.audioapp.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lattice.audioapp.R;
import com.lattice.audioapp.adapters.RVAdapter;
import com.lattice.audioapp.models.Song;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Local extends Fragment {

    @BindView(R.id.local_recycler_view)
    RecyclerView localRecyclerView;

    RVAdapter rvAdapter;
    List<Song> songList;
    LinearLayoutManager linearLayoutManager;
    createDataParse createDataParse;
    private ContentResolver contentResolver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        ButterKnife.bind(this,view);

        contentResolver = getContext().getContentResolver();

        linearLayoutManager = new LinearLayoutManager(getContext());
        songList = new ArrayList<>();
        localRecyclerView.setLayoutManager(linearLayoutManager);
        rvAdapter = new RVAdapter(songList, getContext());
        localRecyclerView.setAdapter(rvAdapter);

        getMusic();

        rvAdapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song, int position) {
                createDataParse.onDataPass(songList.get(position).getTitle(), songList.get(position).getSource());
                createDataParse.fullSongList(songList, position);
            }
        });

        return view;
    }

    public void getMusic() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                songList.add(new Song(songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getString(songPath)));
            } while (songCursor.moveToNext());
            songCursor.close();

            rvAdapter.notifyDataSetChanged();
        }

    }

    public interface createDataParse {
        public void onDataPass(String name, String path);

        public void fullSongList(List<Song> songList, int position);

        public void currentSong(Song songsList);
        public void getLength(int length);
    }

}
