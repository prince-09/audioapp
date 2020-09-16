package com.lattice.audioapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lattice.audioapp.R;
import com.lattice.audioapp.adapters.RVAdapter;
import com.lattice.audioapp.models.Results;
import com.lattice.audioapp.models.Song;
import com.lattice.audioapp.services.ApiClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feed extends Fragment {


    @BindView(R.id.feed_recycler_view)
    RecyclerView feedRecyclerView;
    RVAdapter rvAdapter;
    List<Song> songList;
    LinearLayoutManager linearLayoutManager;
    createDataParse createDataParse;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this,view);

        linearLayoutManager = new LinearLayoutManager(getContext());
        songList = new ArrayList<>();
        feedRecyclerView.setLayoutManager(linearLayoutManager);
        rvAdapter = new RVAdapter(songList, getContext());
        feedRecyclerView.setAdapter(rvAdapter);

        rvAdapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song, int position) {
                createDataParse.onDataPass(songList.get(position).getTitle(), songList.get(position).getSource());
                createDataParse.fullSongList(songList, position);
            }
        });


        populate();
        return view;
    }



    private void populate() {
        Call<Results> call = ApiClient.getInstance().getApi().getResults();
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                Log.i("song",response.isSuccessful()+" "+response.raw());
                if(response.isSuccessful()&&response.body()!=null){
                    songList.addAll(response.body().getSongList());
                    rvAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

            }
        });
    }

    public interface createDataParse {
        public void onDataPass(String name, String path);

        public void fullSongList(List<Song> songList, int position);

        public void currentSong(Song songsList);
        public void getLength(int length);
    }

}
