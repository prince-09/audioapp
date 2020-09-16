package com.lattice.audioapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lattice.audioapp.R;
import com.lattice.audioapp.models.Song;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyView> {

    List<Song> songList;
    Context context;
    boolean isPlaying;
    OnItemClickListener mOnItemClickListener;



    public RVAdapter(List<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Song song, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        Song song = songList.get(position);
        if(song.getSource()!=null&&!song.getSource().equals(""))
        Glide.with(context).load(song.getImage()).into(holder.imageSong);
        holder.nameOfSong.setText(song.getTitle());
        holder.imageSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(song, position);
            }
        });
        holder.nameOfSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(song, position);
            }
        });

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFileAsync().execute(song.getSource());
            }
        });

    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("song","download start");
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                InputStream input = new BufferedInputStream(url.openStream());
                Log.i("song","downloading");
                String root = Environment.getExternalStorageDirectory().toString();
                File new_folder = new File(root + "/audioapp");
                if (!new_folder.exists()) {
                    new_folder.mkdir();
                }

                File file = new File(new_folder, "audioA"+System.currentTimeMillis() + ".mp3");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("song","downloading  "+file.getAbsolutePath());
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.i("song","downloading  "+e.toString());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);

        }

        @Override
        protected void onPostExecute(String unused)
        {
            Toast.makeText(context,"Downloaded",Toast.LENGTH_SHORT).show();
            Log.i("song","downloaded");
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        @BindView(R.id.image_song)
        ImageView imageSong;
        @BindView(R.id.name_of_song)
        TextView nameOfSong;
        @BindView(R.id.download_button)
        ImageView downloadButton;

        public MyView(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
