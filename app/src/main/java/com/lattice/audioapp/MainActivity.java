package com.lattice.audioapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lattice.audioapp.fragments.Downloaded;
import com.lattice.audioapp.fragments.Feed;
import com.lattice.audioapp.fragments.Local;
import com.lattice.audioapp.models.Song;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Feed.createDataParse, View.OnClickListener, Local.createDataParse {

    @BindView(R.id.mainpagetabs)
    TabLayout mainpagetabs;
    @BindView(R.id.mainviewpager)
    ViewPager mainviewpager;
    List<Song> songList;
    Song song;
    int songLength;
    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;

    MainViewPagerAdapter viewPagerAdapter;
    @BindView(R.id.name_of_song)
    TextView nameOfSong;
    @BindView(R.id.previous)
    ImageView previous;
    @BindView(R.id.play_pause)
    ImageView playPause;
    @BindView(R.id.skip)
    ImageView skip;
    @BindView(R.id.playerCL)
    ConstraintLayout playerCL;
    @BindView(R.id.seekbar_controller)
    SeekBar seekbarController;
    private int currentPosition;
    private boolean playContinueFlag;
    private int playlistFlag;
    private boolean checkFlag;
    private final int MY_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mediaPlayer = new MediaPlayer();

        grantedPermission();
    }

    public void setUpViewPager(){
        viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainviewpager.setOffscreenPageLimit(2);
        mainviewpager.setAdapter(viewPagerAdapter);
        mainpagetabs.setupWithViewPager(mainviewpager);
    }

    private void attachMusic(String name, String path) {
        playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        nameOfSong.setText(name);
        Log.i("song", "music attached");
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            setControls();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                if (playContinueFlag) {
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getSource());
                        currentPosition += 1;
                    } else {
                        Toast.makeText(MainActivity.this, "PlayList Ended", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setControls() {
        seekbarController.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        playCycle();
        checkFlag = true;
        if (mediaPlayer.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);

        }

        seekbarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void playCycle() {
        try {
            seekbarController.setProgress(mediaPlayer.getCurrentPosition());

            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();

                    }
                };
                handler.postDelayed(runnable, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataPass(String name, String path) {
        Log.i("song", "onAttach");
        attachMusic(name, path);
    }

    @Override
    public void fullSongList(List<Song> songList, int position) {
        this.songList = songList;
        this.currentPosition = position;
        this.playlistFlag = songList.size();

    }

    @Override
    public void currentSong(Song songsList) {

    }

    @Override
    public void getLength(int length) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        playPause.setOnClickListener(this);
        skip.setOnClickListener(this);
        previous.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause:
                if (checkFlag) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    } else if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                        playCycle();
                    }
                } else {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.skip:
                if (checkFlag) {
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getSource());
                        currentPosition += 1;
                    } else {
                        Toast.makeText(this, "Playlist Ended", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.previous:
                if (checkFlag) {
                    if (mediaPlayer.getCurrentPosition() > 10) {
                        if (currentPosition - 1 > -1) {
                            attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getSource());
                            currentPosition = currentPosition - 1;
                        } else {
                            attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getSource());
                        }
                    } else {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getSource());
                    }
                } else {
                    Toast.makeText(this, "Select a Song . .", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public class MainViewPagerAdapter extends FragmentPagerAdapter {


        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new Feed();
            } else if (position == 1) {
                fragment = new Local();
            } else if (position == 2) {
                fragment = new Downloaded();
            }


            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0) {
                title = "Feed";
            } else if (position == 1) {
                title = "Local";
            } else if (position == 2) {
                title = "Downloaded";
            }

            return title;
        }


    }

    private void grantedPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    setUpViewPager();
                }
            }
        }else{
            setUpViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        setUpViewPager();
                    } else {

                        finish();
                    }
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
